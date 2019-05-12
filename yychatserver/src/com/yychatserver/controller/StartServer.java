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
try {//捕获异常
ss= new ServerSocket(3456);
System.out.println("服务器已经启动，监听3456端口");
while(true){//?Thread多线程
s= ss.accept();//接收客户端连接请求
System.out.println("连接成功:"+s);

//接收User对象
ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
User user=(User)ois.readObject();
userName=user.getUserName();
passWord=user.getPassWord();
System.out.println(userName);
System.out.println(passWord);


//使用数据库进行用户身份认证
//1、加载驱动程序
Class.forName("com.mysql.jdbc.Driver");
System.out.println("已经加载了数据库驱动！");
//2、连接数据库
String url="jdbc:mysql://127.0.0.1:3306/yychat";
//中文用户名必须用下面的url
//String url="jdbc:mysql://127.0.0.1:3306/yychat?useUnicode=true&characterEncoding=UTF-8";
String dbUser="root";
String dbPass="";
Connection conn=DriverManager.getConnection(url,dbUser,dbPass);

//3、创建PreparedStatement对象，用来执行SQL语句
String user_Login_Sql="select * from user where username=? and password=?";
PreparedStatement ptmt=conn.prepareStatement(user_Login_Sql);
ptmt.setString(1, userName);
ptmt.setString(2, passWord);

//4、执行查询，返回结果集
ResultSet rs=ptmt.executeQuery();

//5、根据结果集来判断是否能登录
boolean loginSuccess=rs.next();

//实现密码验证功能
Message mess=new Message();
mess.setSender("Server");
mess.setReceiver(userName);
if(loginSuccess){//对象比较
//告诉客户端密码验证通过的消息，可以创建Message类
mess.setMessageType(Message.message_LoginSuccess);//"1"为验证通过

String friend_Relation_Sql="select slaveuser from relation relation where majoruser=? and relationtype='1'";
ptmt=conn.prepareStatement(friend_Relation_Sql);
ptmt.setString(1, userName);
rs= ptmt.executeQuery();
String friendString = "";
while(rs.next()){
friendString = friendString + rs.getString("slaveuser")+"";

}
mess.setContent(friendString);
System.out.println(userName+"的relation数据表中的好友："+friendString);

}else {
mess.setMessageType(Message.message_LoginFailure);//"0"为验证不通过
}
sendMessage(s,mess);
ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
oos.writeObject(mess);

//在这里接收聊天信息，可不可以？不可以，应该新建一个接收线程
if(loginSuccess){
hmSocket.put(userName, s);
new ServerReceiverThread(s).start();//就绪,每个用户都有一个对应的服务线程
}
}

} catch (IOException e) {
e.printStackTrace();//处理异常
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