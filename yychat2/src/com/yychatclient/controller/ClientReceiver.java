package com.yychatclient.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.yychat.model.Message;
import yychat2.FriendChat1;
import yychat2.Friendlist;

public class ClientReceiver extends Thread{
	
	private Socket s;
	
	public ClientReceiver(Socket s){
		this.s=s;
} 
	public void run(){
		ObjectInputStream ois;
		while(true){
		try {
			ois=new ObjectInputStream(s.getInputStream());
			Message mess=(Message)ois.readObject();
			String showMessage=mess.getSender()+"��"+mess.getReceiver()+"˵��"+mess.getContent();
			System.out.println(showMessage);
			//jta.append(showMessage+"\r\n");
			FriendChat1 friendChat1=(FriendChat1)Friendlist.hmFriendChat1.get(mess.getReceiver()+"to"+mess.getSender());
			friendChat1.appendJta(showMessage);
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		}
	}
}
