package com.yychatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yychat.model.Message;
import com.yychat.model.User;

public class StartServer {
public static HashMap hmSocket=new HashMap<String,Socket>();

ServerSocket ss;
Socket s;
String userName;
String passWord;
public StartServer(){
try {//�����쳣
ss= new ServerSocket(3456);
System.out.println("�������Ѿ�����������3456�˿�");
while(true){//?Thread���߳�
s= ss.accept();//���տͻ�����������
System.out.println("���ӳɹ�:"+s);

//����User����
ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
User user=(User)ois.readObject();
userName=user.getUserName();
passWord=user.getPassWord();
System.out.println(userName);
System.out.println(passWord);


//ʹ�����ݿ�����û������֤
//1��������������
Class.forName("com.mysql.jdbc.Driver");
System.out.println("�Ѿ����������ݿ�������");
//2���������ݿ�
String url="jdbc:mysql://127.0.0.1:3306/yychat";
//�����û��������������url
//String url="jdbc:mysql://127.0.0.1:3306/yychat?useUnicode=true&characterEncoding=UTF-8";
String dbUser="root";
String dbPass="";
Connection conn=DriverManager.getConnection(url,dbUser,dbPass);

//3������PreparedStatement��������ִ��SQL���
String user_Login_Sql="select * from user where username=? and password=?";
PreparedStatement ptmt=conn.prepareStatement(user_Login_Sql);
ptmt.setString(1, userName);
ptmt.setString(2, passWord);

//4��ִ�в�ѯ�����ؽ����
ResultSet rs=ptmt.executeQuery();

//5�����ݽ�������ж��Ƿ��ܵ�¼
boolean loginSuccess=rs.next();

//ʵ��������֤����
Message mess=new Message();
mess.setSender("Server");
mess.setReceiver(userName);
if(loginSuccess){//����Ƚ�
//���߿ͻ���������֤ͨ������Ϣ�����Դ���Message��
mess.setMessageType(Message.message_LoginSuccess);//"1"Ϊ��֤ͨ��

String friend_Relation_Sql="select slaveuser from relation relation where majoruser=? and relationtype='1'";
ptmt=conn.prepareStatement(friend_Relation_Sql);
ptmt.setString(1, userName);
rs= ptmt.executeQuery();
String friendString = "";
while(rs.next()){
friendString = friendString + rs.getString("slaveuser")+"";

}
mess.setContent(friendString);
System.out.println(userName+"��relation���ݱ��еĺ��ѣ�"+friendString);

}else {
mess.setMessageType(Message.message_LoginFailure);//"0"Ϊ��֤��ͨ��
}
sendMessage(s,mess);
ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
oos.writeObject(mess);

//���������������Ϣ���ɲ����ԣ������ԣ�Ӧ���½�һ�������߳�
if(loginSuccess){
hmSocket.put(userName, s);
new ServerReceiverThread(s).start();//����,ÿ���û�����һ����Ӧ�ķ����߳�
}
}

} catch (IOException e) {
e.printStackTrace();//�����쳣
} catch (ClassNotFoundException e) {
e.printStackTrace();
} catch (SQLException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
}
private void sendMessage(Socket s2, Message mess) {
// TODO Auto-generated method stub

}
}