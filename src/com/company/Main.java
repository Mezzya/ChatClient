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

				Message m = new Message(login, text);
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
			System.out.println(http.getContentType());
			System.out.println(http.getResponseCode());
				if (http.getResponseCode()==HttpURLConnection.HTTP_OK) return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;



	}
}
