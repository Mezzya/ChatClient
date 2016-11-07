package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {
    private static CookieManager cm;
    static {
        cm = new CookieManager();
        CookieHandler.setDefault(cm);
    }
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String login;
		String password;
		try {
			do {
				System.out.println("Enter your login: ");
				login = scanner.nextLine();
				System.out.println("Enter your password: ");
				password = scanner.nextLine();

			} while (!login(login,password));

			Thread th = new Thread(new GetThread(cm));
			th.setDaemon(true);
			th.start();

            System.out.println("Enter your message: ");
			while (true) {

				String text = scanner.nextLine();
				if (text.isEmpty()) break;

				if (text.equals("-getusers"))
				{
//					Запрос на онлайн пользователей
					getUserOnline();
					continue;
				}

				if (text.equals("-private"))
				{
//					Приватное сообщение
					System.out.println("Enter user to:");
					String to = scanner.nextLine();
					System.out.println("Enter message:");
					text = scanner.nextLine();
					Message m = new Message(login, to, text);
					int res = m.send(Utils.getURL() + "/add");

					if (res != 200) { // 200 OK
						System.out.println("HTTP error occured: " + res);
						return;
					}
					System.out.println("Message Send to user "+to);
					continue;
				}



				Message m = new Message(login, text);
				System.out.println("Out msg");
				int res = m.send(Utils.getURL() + "/add");

				if (res != 200) { // 200 OK
					System.out.println("HTTP error occured: " + res);
					return;
				}

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
	public static boolean login(String login, String password)
	{

		try {
			URL url = new URL(Utils.getURL() + "/login?login="+login+"&password="+password);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			if (http.getResponseCode()==HttpURLConnection.HTTP_OK) return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Error connect server... Try again...");
		return false;



	}

	public static void getUserOnline()
	{

		try{
		URL url = new URL(Utils.getURL() + "/getusers");
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
			System.out.println(http.getResponseCode());
	} catch (IOException e) {
		e.printStackTrace();
	}


}
}
