/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package loginregisterbmichat;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author user
 */
public class Server extends javax.swing.JFrame {

    /**
     * Creates new form Server
     */
    
    ServerSocket ss;
    HashMap<String, Socket> clientColl = new HashMap<>();
    HashMap<String, Socket> blockedClients = new HashMap<>();
    private Set<String> blockedUsers = new HashSet<>();
    private Set<String> connectedUsers = new HashSet<>();

    
    public Server() {
        try {
            initComponents();
            ss = new ServerSocket(2089);
            this.sStatus.setText("Server berjalan!");
            new ClientAccept().start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateUserList() {
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            Set<String> keys = clientColl.keySet();
            for (String key : keys) {
                listModel.addElement(key);
            }
            Chat.UL.setModel(listModel);
        });
    }
    
    private void blockUser(String username) {
        // Tambahkan pengguna ke daftar pengguna yang diblokir
        blockedUsers.add(username);
    }
   
    private boolean isUserBlocked(String username) {
        // Periksa apakah pengguna berada dalam daftar pengguna yang diblokir
        return blockedUsers.contains(username);
    }
    
    
    public synchronized void blockUser(String username, Socket clientSocket) {
        blockedClients.put(username, clientSocket);
        updateUserList();
    }

    class ClientAccept extends Thread {
        public void run() {
            while (true) {
                try {
                    Socket s = ss.accept();
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    if (clientColl.containsKey(i)) {
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF("Anda sudah terdaftar");
                    } else {
                        if (blockedClients.containsKey(i)) {
                            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                            dout.writeUTF("Anda telah diblokir");
                            s.close();
                        } else {
                            clientColl.put(i, s);
                            msgBox.append(i + " Bergabung \n");
                            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                            dout.writeUTF(" ");
                            new MsgRead(s, i).start();
                            new PrepareClientList().start();
                            updateUserList();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream din;
        private DataOutputStream dout;
        private String clientID;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                din = new DataInputStream(clientSocket.getInputStream());
                dout = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleBlockRequest(String message) {
            String usernameToBlock = message.substring(8); // Get the username to block from the message
            blockUser(usernameToBlock);
        }

        public void run() {
            try {
                while (true) {
                    String message = din.readUTF();
                    if (message.startsWith("<BLOKIR>")) {
                        handleBlockRequest(message);
                    } else {
                        // Handle other messages here (e.g., broadcast or private messages)
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    
    class MsgRead extends Thread {
        Socket s;
        String ID;

        MsgRead(Socket s, String ID) {
            this.s = s;
            this.ID = ID;
        }

        public void run() {
            while (!clientColl.isEmpty()) {
                try {
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    msgBox.append("Pesan dari klien " + ID + ": " + i + "\n");

                    if (i.startsWith("#4344554@@@@@67667@@")) {
                        i = i.substring(20);
                        StringTokenizer st = new StringTokenizer(i, ":");
                        String receiverID = st.nextToken();
                        i = st.nextToken();
                        sendPrivateMessage(ID, receiverID, i);
                    } else {
                        // Tambahkan logika untuk mengabaikan pesan dari pengguna yang diblokir
                        if (!blockedUsers.contains(ID)) {
                            sendToAllClients(ID, i);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    clientColl.remove(ID);
                    msgBox.append(ID + " dihapus!");
                    new PrepareClientList().start();
                    break;
                }
            }
    }
    
//    private void sendToAllClients(String senderID, String message) {
//            Set<String> keys = clientColl.keySet();
//                    for (String key : keys) {
//                        if (!key.equalsIgnoreCase(senderID)) {
//                            try {
//                                new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream())
//                                        .writeUTF("< " + senderID + " ke semuanya > " + message);
//                            } catch (IOException ex) {
//                                clientColl.remove(key);
//                                msgBox.append(key + " dihapus!");
//                                new PrepareClientList().start();
//                            }
//                        }
//                    }
//    }
        
        private void sendToAllClients(String senderID, String message) {
        Set<String> keys = clientColl.keySet();
        for (String key : keys) {
            try {
                new DataOutputStream(clientColl.get(key).getOutputStream())
                        .writeUTF("< " + senderID + " ke semuanya > " + message);
            } catch (IOException ex) {
                clientColl.remove(key);
                msgBox.append(key + " dihapus!");
                new PrepareClientList().start();
            }
        }
    }

        private void sendPrivateMessage(String senderID, String receiverID, String message) {
            try {
                if (!receiverID.isEmpty()) {
                    if (!blockedUsers.contains(receiverID)) {
                        new DataOutputStream(clientColl.get(receiverID).getOutputStream())
                                .writeUTF("<Pesan pribadi dari " + senderID + "> " + message);
                        sendText.setText("");
                        msgBox.append("<Pesan pribadi dari " + senderID + " kepada " + receiverID + "> " + message + "\n");
                    } else {
                        JOptionPane.showMessageDialog(Server.this, "Penerima pesan diblokir!");
                    }
                } else {
                    JOptionPane.showMessageDialog(Server.this, "Pilih pengguna terlebih dahulu!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                clientColl.remove(receiverID);
                msgBox.append(receiverID + " dihapus!");
                new PrepareClientList().start();
            }
        }
    }

    class PrepareClientList extends Thread {
        public void run () {
            try {
                String ids = "";
                Set k = clientColl.keySet();
                Iterator itr = k.iterator();
                while (itr.hasNext()){
                    String key = (String)itr.next();
                    ids+=key+",";
                }
                if (ids.length() != 0) {
                    ids = ids.substring(0, ids.length() - 1);
                }
                itr = k.iterator();
                List<String> clientsToRemove = new ArrayList<>();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF(":;.,/=" + ids);
                    } catch (Exception ex) {
                        clientsToRemove.add(key);
                        msgBox.append(key + "Dihapus!");
                    }
                }

                // Remove clients that encountered exceptions (e.g., logged out or disconnected)
                for (String key : clientsToRemove) {
                    clientColl.remove(key);
                }
                updateUserList();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        sStatus = new javax.swing.JLabel();
        sendText = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(153, 153, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 500));
        jPanel2.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 60)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Pantau");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(34, 24, 260, 70);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 55)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Server");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(34, 100, 172, 64);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Melihat dunia berbicara");
        jPanel2.add(jLabel6);
        jLabel6.setBounds(34, 170, 187, 22);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("itu menyenangkan!");
        jPanel2.add(jLabel8);
        jLabel8.setBounds(34, 198, 152, 22);

        jLabel9.setIcon(new javax.swing.ImageIcon("/Users/altamis/NetBeansProjects/BMI-Project-17Juli/Assets/homepage.png")); // NOI18N
        jPanel2.add(jLabel9);
        jLabel9.setBounds(76, 226, 250, 250);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 400, 500);

        jPanel3.setBackground(new java.awt.Color(0, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 500));

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("Server Status:");

        sStatus.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        sStatus.setText("........................................");

        sendButton.setText("Kirim");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setText("Kirim pesan ke client");

        jLabel4.setText("Gunakan @'idClient' untuk mengirim pesan pribadi ke klien");

        jLabel5.setText("Contoh");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("@User: halo");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7))
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(sStatus))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sStatus))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(400, 0, 400, 500);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        try {
            String m = sendText.getText();

            if (!clientColl.isEmpty()) {
                if (m.startsWith("@")) {
                    String[] parts = m.split(":", 2);
                    String receiverID = parts[0].substring(1); 
                    m = parts[1];

                    if (clientColl.containsKey(receiverID)) {
                        sendPrivateMessage("Server", receiverID, m);
                        sendText.setText("");
                        msgBox.append("<Server> Pesan pribadi ke " + receiverID + ": " + m + "\n");
                    } else {
                        JOptionPane.showMessageDialog(this, "Penerima pesan tidak ditemukan!");
                    }
                } else {
                    sendToAllClients("Server", m);
                    sendText.setText("");
                    msgBox.append("<Server> Pesan ke semua: " + m + "\n");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada klien yang terhubung!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengirim pesan!");
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void sendToAllClients(String senderID, String message) {
        Set k = clientColl.keySet();
        Iterator itr = k.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            if (!key.equalsIgnoreCase(senderID)) {
                try {
                    new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream())
                            .writeUTF("< " + senderID + " ke semuanya > " + message);
                } catch (Exception ex) {
                    clientColl.remove(key);
                    msgBox.append(key + " dihapus!");
                    new PrepareClientList().start();
                }
            }
        }
    }

    
    private void sendPrivateMessage(String senderID, String receiverID, String message) {
        try {
            if (!receiverID.isEmpty()) {
                String m = receiverID + ":" + message;
                new DataOutputStream(((Socket) clientColl.get(receiverID)).getOutputStream())
                        .writeUTF("<Pesan pribadi dari " + senderID + "> " + message);
                sendText.setText("");
                msgBox.append("<Pesan pribadi dari " + senderID + " kepada " + receiverID + "> " + message + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih pengguna terlebih dahulu!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            clientColl.remove(receiverID);
            msgBox.append(receiverID + " dihapus!");
            new PrepareClientList().start();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTextArea msgBox;
    private javax.swing.JLabel sStatus;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField sendText;
    // End of variables declaration//GEN-END:variables
}
