package org.example.crm_system;

import org.example.crm_system.entity.MyBot;
import org.example.crm_system.entity.SpringContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class CrmSystem {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(CrmSystem.class, args);
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		telegramBotsApi.registerBot(SpringContext.getBean(MyBot.class));
	}
}
