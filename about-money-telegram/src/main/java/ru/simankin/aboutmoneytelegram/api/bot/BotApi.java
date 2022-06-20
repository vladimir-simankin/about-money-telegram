package ru.simankin.aboutmoneytelegram.api.bot;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.simankin.aspect.logging.annotation.Logging;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotApi extends TelegramLongPollingBot {

  private static final String TOKEN = "";
  private static final String USERNAME = "";

  @Override
  public String getBotUsername() {
    return USERNAME;
  }

  @Override
  public String getBotToken() {
    return TOKEN;
  }

  @Logging
  @Override
  public void onUpdateReceived(Update update) {}

  @Logging
  @Override
  public void onUpdatesReceived(List<Update> updates) {
    for (Update update : updates) {
      try {
        String chatId = String.valueOf(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        String response;
        if ("/start".equals(text)) {
          response =
              "Для добавления операции нужно использовать формат:\n { \n[расход или доход]\n[категория]\n[место]\n[сумма]\n}";
        } else {
          response = getText(text);
        }
        SendMessage sendMessage = new SendMessage(chatId, response);
        execute(sendMessage);
      } catch (TelegramApiException e) {
        log.error("Error processing message: {}", e.getMessage(), e);
      }
    }
  }

  private String getText(String text) {
    if (isNotBlank(text)) {
      String[] splintedText = text.split(" ");
      if (splintedText.length == 4) {
        String operation = splintedText[0];
        String category = splintedText[1];
        String place = splintedText[2];
        int amount = Integer.parseInt(splintedText[3]);
        return "Обработан "
            + operation
            + ":\n {\n категория ["
            + category
            + "]\n место ["
            + place
            + "]\n сумма ["
            + amount
            + "] \n}";
      }
    }
    return "Ошибка при обработке сообщения: {"
        + text
        + "} \nКорректный формат:\n { \n[расход или доход]\n[категория]\n[место]\n[сумма]\n}";
  }
}
