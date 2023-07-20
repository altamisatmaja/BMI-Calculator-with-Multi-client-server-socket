package loginregisterbmichat;

import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

public class Chat extends javax.swing.JFrame {
    
    String iD, clientID = "";
        
    DataInputStream din;
    DataOutputStream dout;
    DefaultListModel dlm;
    private Set<String> blockedUsers = new HashSet<>();
    private Set<String> connectedUsers = new HashSet<>();
    
        
    public Chat() {
        initComponents();
    }
    
    Chat(String i, Socket s) {
        iD = i;
        try {
            initComponents();
            dlm = new DefaultListModel();
            UL.setModel(dlm);
            idlabel.setText(i);
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            new Read().start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void blockUser(String username) {
        try {
            String blockMessage = "<BLOKIR>" + username;
            dout.writeUTF(blockMessage);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memblokir pengguna!");
        }
    }

    
    class Read extends Thread {
        public void run() {
            while(true) {
                try{
                    String m = din.readUTF();
                    if(m.contains(":;.,/=")){
                        m = m.substring(6);
                        dlm.clear();
                        StringTokenizer st = new StringTokenizer(m, ",");
                        while(st.hasMoreTokens()){
                            String u = st.nextToken();
                            if(!iD.equals(u)){
                                dlm.addElement(u);
                            }
                        }
                        UL.setModel(dlm);
                    }
                    else if (m.startsWith("< ")) {
                        int endNameIndex = m.indexOf(" ke ");
                        String senderName = m.substring(2, endNameIndex);
                        String messageContent = m.substring(endNameIndex + 4);
                        if (!blockedUsers.contains(senderName)) {
                            msgBox.append("<Pesan dari " + senderName + messageContent + "\n");
                        } else {
                            msgBox.append("<Pesan dari " + senderName + " diblokir>\n");
                        }
                    }
                    else if (m.startsWith("<BLOKIR>")) {
                        String blockedUser = m.substring(8);
                        blockedUsers.add(blockedUser);
                        if (iD.equals(blockedUser)) {
                            msgBox.append("<Anda telah diblokir oleh pengguna " + clientID + ">\n");
                        }
                    }
                    else{
                        msgBox.append(""+m+"\n");
                    }
                }
                catch (Exception ex) {
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        idlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        sendText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        UL = new javax.swing.JList<>();
        jTextField1 = new javax.swing.JTextField();
        btnSendPrivateMessage = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        blockUser = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 500));
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 500));
        jPanel1.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 60)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Chat");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(34, 24, 133, 70);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 55)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Application");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(34, 100, 301, 64);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Bagikan pengalaman penggunamu");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(34, 170, 279, 22);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("kepada semua orang di dunia!");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(34, 198, 242, 22);

        jLabel9.setIcon(new javax.swing.ImageIcon("/Users/altamis/NetBeansProjects/BMI-Project-17Juli/Assets/homepage.png")); // NOI18N
        jPanel1.add(jLabel9);
        jLabel9.setBounds(76, 226, 250, 250);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 400, 500);

        jPanel2.setBackground(new java.awt.Color(0, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 500));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setText("Anda masuk sebagai:");

        idlabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        idlabel.setText("</nama>");

        msgBox.setColumns(20);
        msgBox.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        sendButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        sendButton.setText("Kirim");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setText("Masukkan pesan Anda");

        jScrollPane2.setViewportView(UL);

        btnSendPrivateMessage.setText("Kirim");
        btnSendPrivateMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendPrivateMessageActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setText("Masukkan pesan secara pribadi");

        blockUser.setText("Blokir");
        blockUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blockUserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSendPrivateMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1)
                                        .addComponent(sendText))
                                    .addComponent(jLabel1)
                                    .addComponent(idlabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(blockUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(29, 29, 29))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(idlabel)
                    .addComponent(blockUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendButton)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSendPrivateMessage))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(400, 0, 400, 500);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
            try {
                String m = sendText.getText(), mm = m;
                String CI = clientID;

                if (!clientID.isEmpty()) {
                    // Pesan pribadi dimulai dengan karakter "#" sebagai penanda
                    if (m.startsWith("#")) {
                        m = "#4344554@@@@@67667@@" + CI + ":" + mm;
                        dout.writeUTF(m);
                        sendText.setText("");
                        msgBox.append("<Kamu mengirimkan pesan ke " + CI + ">" + mm + "\n");
                    } else if (clientID.isEmpty()) {
                        // Pesan umum dikirim ke semua orang
                        dout.writeUTF(m);
                        sendText.setText("");
                        msgBox.append("<Ke semua>" + mm + "\n");
                    } else {
                        JOptionPane.showMessageDialog(this, "Tidak ada user!");
                    }
                } else if (clientID.isEmpty()) {
                    dout.writeUTF(m);
                    sendText.setText("");
                    msgBox.append("<Ke semua>" + mm + "\n");
                    // JOptionPane.showMessageDialog(this, "Tidak ada user!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal mengirim pesan!");
            }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void btnSendPrivateMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendPrivateMessageActionPerformed
        String selectedUser = UL.getSelectedValue();
        String message = jTextField1.getText();

        // Pesan pribadi dimulai dengan karakter "#" sebagai penanda
        if (!selectedUser.isEmpty() && !message.isEmpty()) {
            if (blockedUsers.contains(selectedUser)) {
                JOptionPane.showMessageDialog(this, "Anda tidak dapat mengirim pesan pribadi ke pengguna yang diblokir!");
                return;
            }

            String m = "#4344554@@@@@67667@@" + selectedUser + ":" + message;

            try {
                dout.writeUTF(m);
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
            jTextField1.setText("");
            msgBox.append("<Pesan pribadi kepada " + selectedUser + "> " + message + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "Pilih pengguna dan masukkan pesan terlebih dahulu!");
        }
    }//GEN-LAST:event_btnSendPrivateMessageActionPerformed

    private void blockUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blockUserActionPerformed
        String selectedUser = UL.getSelectedValue();
        if (selectedUser != null && !selectedUser.isEmpty()) {
            if (selectedUser.equals(iD)) {
                JOptionPane.showMessageDialog(this, "Tidak dapat memblokir diri sendiri!");
                return;
            }

            blockedUsers.add(selectedUser);
            blockUser(selectedUser);
            JOptionPane.showMessageDialog(this, "Pengguna telah diblokir");
        } else {
            JOptionPane.showMessageDialog(this, "Pilih pengguna terlebih dahulu!");
        }
    }//GEN-LAST:event_blockUserActionPerformed

    private void sendPrivateMessage(String receiverID, String message) {
        try {
            if (!receiverID.isEmpty()) {
                String m = receiverID + ":" + message;
                dout.writeUTF(m);
                jTextField1.setText("");
                msgBox.append("<Pesan pribadi kepada " + receiverID + "> " + message + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih pengguna terlebih dahulu!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengirim pesan pribadi!");
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
            java.util.logging.Logger.getLogger(Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Chat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JList<String> UL;
    private javax.swing.JButton blockUser;
    private javax.swing.JButton btnSendPrivateMessage;
    private javax.swing.JLabel idlabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private static javax.swing.JTextArea msgBox;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField sendText;
    // End of variables declaration//GEN-END:variables
}
