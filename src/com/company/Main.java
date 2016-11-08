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
		String room="All";
		try {
			do {
				System.out.println("Enter your login: ");
				login = scanner.nextLine();
				System.out.println("Enter your password: ");
				password = scanner.nextLine();

			} while (sendToServer("/login?login="+login+"&password="+password)!=200);

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
					int result =sendToServer("/getusers");
					if (result!=200) System.out.println("Error. code : "+result);
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

				if (text.equals("-roomin"))
				{
//					Входим в комнату
					System.out.println("Enter room in:");
					room = scanner.nextLine();

					int result =sendToServer("/room?room="+room);
					if (result!=200) System.out.println("Error. code : "+result);
					else System.out.println("Enter to room :"+room);

					continue;
				}

				if (text.equals("-roomout"))
				{
//					Выходим из комнаты

					int result =sendToServer("/room?room=exit");
					if (result!=200) System.out.println("Error. code : "+result);
					else System.out.println("Exit from room : "+room);
					room="All";
					continue;
				}

				Message m = new Message(login,"All",room, text);

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

	public static int sendToServer(String param)
	{
		try{
			URL url = new URL(Utils.getURL() + param);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			return http.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;

	}
}
