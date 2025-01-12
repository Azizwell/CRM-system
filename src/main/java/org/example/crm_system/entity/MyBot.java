package org.example.crm_system.entity;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.crm_system.filter.UserFilter;
import org.example.crm_system.projection.GetTransactionBySelectedSetting;
import org.example.crm_system.projection.MonthlyExpensProjection;
import org.example.crm_system.projection.MonthlyIncomeProjection;
import org.example.crm_system.repository.TransactionRepo;
import org.example.crm_system.repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {
  final AuthenticationManager authenticationManager;
  final UserRepo userRepo;
  final TransactionRepo transactionRepo;
  final PasswordEncoder passwordEncoder;

  private final Map<Long, String> userStates = new HashMap<>();
  private final Map<Long, UserFilter> userFilters = new HashMap<>();

  Optional<User> selectUser = Optional.empty();
  private boolean exists;
  private boolean exists1;

  @Override
  public void onUpdatesReceived(List<Update> updates) {
    super.onUpdatesReceived(updates);
  }

  //название бота: Crm_test
  @Override
  public String getBotUsername() {
    return "Crm_test123_bot";
  }

  @Override
  public String getBotToken() {
    return "7703237297:AAF2C0p_URGi_QxmV_bGbBPOLyRhpmG05SQ";
  }

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      Long chatId = message.getChatId();
      String userState = userStates.getOrDefault(chatId, "START");

      if (userState.equals("START")) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Salom botga xush kelibsiz! Iltimos, botga kirish uchun login yuboring.");
        sendMessage.setChatId(chatId);
        execute(sendMessage);

        userStates.put(chatId, "WAITING_FOR_LOGIN");
      } else if (userState.equals("WAITING_FOR_LOGIN")) {
        String login = message.getText();

        boolean exists1 = checkLoginInDatabase(login);
        if (exists1) {
          SendMessage sendMessage = new SendMessage();
          sendMessage.setText("Login tasdiqlandi. Iltimos, parolni kiriting.");
          sendMessage.setChatId(chatId);
          userStates.put(chatId, "WAITING_FOR_PASSWORD");
          execute(sendMessage);
          System.out.println(chatId);
        } else {
          SendMessage sendMessage = new SendMessage();
          sendMessage.setText("Kechirasiz, bunday raqam bizda mavjud emas. Qayta urinib ko'ring yoki bizga murojat qiling: http://localhost:5173/");
          sendMessage.setChatId(chatId);
          execute(sendMessage);
        }

      } else if (userState.equals("WAITING_FOR_PASSWORD")) {
        String password = message.getText();
        if (validatePassword(password)) {
          StringBuilder messageBuilder = new StringBuilder();
          String rollName = userRepo.rollName(selectUser.get().getUsername());
          SendMessage sendMessage = new SendMessage();
          sendMessage.enableMarkdown(true);

          if (rollName != null) {

            if (rollName.equals("ROLE_ADMIN")) {
              messageBuilder.append("🎉 *Registratsiya muvaffaqiyatli yakunlandi!* 🎉\n\n");
              messageBuilder.append("👑 *Admin*: ").append(selectUser.get().getFullName()).append("\n");
              messageBuilder.append("📞 *Login*: ").append(selectUser.get().getUsername()).append("\n\n");
              messageBuilder.append("🤝 *Botga xush kelibsiz!*");
              sendMessage.setText(messageBuilder.toString());
              sendMessage.setReplyMarkup(getSettingKeyboard());
              sendMessage.setChatId(chatId);
              userStates.put(chatId, "ADMIN");

            } else if (rollName.equals("ROLE_USER")) {
              messageBuilder.append("🎉 *Registratsiya muvaffaqiyatli yakunlandi!* 🎉\n\n");
              messageBuilder.append("🙋‍♂️ *Foydalanuvchi*: ").append(selectUser.get().getFullName()).append("\n");
              messageBuilder.append("📞 *Login*: ").append(selectUser.get().getUsername()).append("\n\n");
              messageBuilder.append("🤝 *Botga xush kelibsiz!*");
              sendMessage.setText(messageBuilder.toString());
              sendMessage.setReplyMarkup(getSettingKeyboard());
              sendMessage.setChatId(chatId);
              userStates.put(chatId, "USER");
            } else {
              sendMessage.setText("xatolik yuz berdi");
              sendMessage.setChatId(chatId);
              userStates.put(chatId, "START");
            }
            execute(sendMessage);
          } else {
            sendMessage.setText("xatolik yuz berdi");
            sendMessage.setChatId(chatId);
            userStates.put(chatId, "WAITING");
          }
        } else {
          SendMessage sendMessage = new SendMessage();
          sendMessage.setText("Noto'g'ri parol. Qayta urinib ko'ring.");
          sendMessage.setChatId(chatId);
          execute(sendMessage);
        }
      } else if (userState.equals("USER")) {
        SendMessage sendMessage = new SendMessage();
        if (message.getText().equals("Hisobotlar")) {
          sendMessage.setText("Hisobotlar");
          sendMessage.setReplyMarkup(getUserReportPanelInlineKeyboard());

        } else if (message.getText().equals("Sozlamalar")) {
          sendMessage.setText("Sozlamalar");
          sendMessage.setReplyMarkup(getUserSettingPanelInlineKeyboard());
        } else if (message.getText().startsWith("Login")) {
          String text = message.getText();

          int index = text.indexOf("Login");

          if (index != -1) {
            String emailPart = text.substring(index + "Login".length()).trim();
            User user = userRepo.findByUsername(selectUser.get().getUsername()).orElseThrow();
            user.setUsername(emailPart);
            User save = userRepo.save(user);
            selectUser = Optional.of(save);
            sendMessage.setText("Login kiritildi");
          } else {
            sendMessage.setText("Login xato kiritildi");
          }
        } else if (message.getText().startsWith("Name")) {
          String text = message.getText();

          int index = text.indexOf("Name");

          if (index != -1) {
            String namePart = text.substring(index + "Name".length()).trim();
            User user = userRepo.findByUsername(selectUser.get().getUsername()).orElseThrow();
            user.setFullName(namePart);
            User save = userRepo.save(user);
            selectUser = Optional.of(save);
            sendMessage.setText("Name o'zgartirildi");
          } else {
            sendMessage.setText("Name xato kiritildi");
          }
        } else if (message.getText().startsWith("ChangePassword")) {
          String text = message.getText();

          int index = text.indexOf("ChangePassword");

          if (index != -1) {
            String passwordPart = text.substring(index + "ChangePassword".length()).trim();
            User user = userRepo.findByUsername(selectUser.get().getUsername()).orElseThrow();
            user.setPassword(passwordEncoder.encode(passwordPart));
            User save = userRepo.save(user);
            selectUser = Optional.of(save);
            sendMessage.setText("Parol o'zgartirildi");
          } else {
            sendMessage.setText("Parol xato kiritildi");
          }
        }


        sendMessage.setChatId(chatId);
        execute(sendMessage);


      } else if (userState.equals("WAITING")) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("kuting siz bilan bog'lanamiz");
        sendMessage.setChatId(chatId);
        execute(sendMessage);
      }
    } else if (update.hasCallbackQuery()) {
      CallbackQuery callbackQuery = update.getCallbackQuery();
      Long calBackId = callbackQuery.getFrom().getId();
      String data = callbackQuery.getData();
      SendMessage sendMessage = new SendMessage();
      sendMessage.setChatId(calBackId);
      StringBuilder messageBuilder = new StringBuilder();

      UserFilter filter = userFilters.getOrDefault(calBackId, new UserFilter());
      //отчеты
      if (data.equals("monthlyIncome")) {
        List<MonthlyIncomeProjection> monthlyIncome = transactionRepo.getMonthlyIncome();
        ByteArrayInputStream excelStream = generateExcelIncome(monthlyIncome);
        sendMessage.setText("Oylik daromadlar:");
        if (excelStream != null) {
          sendExcelToUser(calBackId, excelStream, "MonthlyIncome.xlsx");
        }
      } else if (data.equals("monthlyExpense")) {
        List<MonthlyExpensProjection> monthlyExpense = transactionRepo.getMonthlyExpense();
        ByteArrayInputStream byteArrayInputStream = generateExcelExpense(monthlyExpense);
        sendMessage.setText("Oylik xarajatlar:");
        if (byteArrayInputStream != null) {
          sendExcelToUser(calBackId, byteArrayInputStream, "MonthlyExpense.xlsx");
        }
      } else if (data.equals("additionalReport")) {
        sendMessage.setText("qo'shimcha malumotlar");
        sendMessage.setReplyMarkup(getUserAdditionalSettingInlineKeyboard());
      } else if (data.equals("filter_type_income")) {
        sendMessage.setText("Income");
        filter.setType("INCOME");
      } else if (data.equals("filter_type_expense")) {
        sendMessage.setText("Expense");
        filter.setType("EXPENSE");
      } else if (data.startsWith("filter_currency_")) {
        sendMessage.setText(data.replace("filter_currency_", "").toUpperCase());
        filter.setCurrency(data.replace("filter_currency_", "").toUpperCase());
      } else if (data.startsWith("month_january")) {
        sendMessage.setText("January");
        filter.setYear(2025);
        filter.setMonth(1);
      } else if (data.equals("month_february")) {
        sendMessage.setText("February");
        filter.setYear(2025);
        filter.setMonth(2);
      } else if (data.equals("month_march")) {
        sendMessage.setText("March");
        filter.setYear(2025);
        filter.setMonth(3);
      } else if (data.equals("month_april")) {
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(4);
      } else if (data.equals("month_may")) {
        sendMessage.setText("May");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(5);
      } else if (data.equals("month_june")) {
        sendMessage.setText("June");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(6);
      } else if (data.equals("month_july")) {
        sendMessage.setText("July");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(7);
      } else if (data.equals("month_august")) {
        sendMessage.setText("August");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(8);
      } else if (data.equals("month_september")) {
        sendMessage.setText("September");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(9);
      } else if (data.equals("month_october")) {
        sendMessage.setText("October");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(10);
      } else if (data.equals("month_november")) {
        sendMessage.setText("November");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(11);
      } else if (data.equals("month_december")) {
        sendMessage.setText("December");
        sendMessage.setText("April");
        filter.setYear(2025);
        filter.setMonth(12);
      } else if (data.equals("save")) {
        sendMessage.setText("Save");
        List<GetTransactionBySelectedSetting> transactions =
                transactionRepo.getTransactionsBySelectedSetting(
                        filter.getYear(),
                        filter.getMonth(),
                        filter.getType(),
                        filter.getCurrency()
                );
        ByteArrayInputStream byteArrayInputStream = generateExcelFilter(transactions);
        sendMessage.setText("Filtrlangan holatdagi:");
        if (byteArrayInputStream != null) {
          sendExcelToUser(calBackId, byteArrayInputStream, "FilterTransactions.xlsx");
        }
        System.out.println(transactions.size());
        System.out.println(filter);
      } else if (data.equals("reset")) {
        sendMessage.setText("reset");
        filter = new UserFilter();
        userFilters.put(calBackId, filter);
      }
      //настройки
      else if (data.equals("changeProfile")) {
        messageBuilder.append("*Login:* ").append(selectUser.get().getUsername() + "\n");
        messageBuilder.append("*FullName:* ").append(selectUser.get().getFullName() + "\n");
        sendMessage.setText(messageBuilder.toString());
        sendMessage.setReplyMarkup(getUserNameOrUserFullNameInlineKeyboard());
      } else if (data.equals("updateLogin")) {
        sendMessage.setText("Login o'zgartirish uchun masalan so'z boshida (Login vali@mail.ru) usulida kiriting ");

      } else if (data.equals("updateName")) {
        sendMessage.setText("Ism kiritish uchun masalan so'z boshida (Name Ali) usulida kiriting ");
      } else if (data.equals("viewAccess")) {

        String enabled = selectUser.get().getIsEnabled() ? "Kirishga ruxsat" : " akkaunt bloklangan ";
        String role = selectUser.get().getRoles().get(0).getName().equals("ROLE_USER") ? "foydalanuvchi" : "boshqa";
        messageBuilder.append("*Ruxasat:* ").append(enabled + "\n");
        messageBuilder.append("*roli:* ").append(role + "\n");
        sendMessage.setText(messageBuilder.toString());
      } else if (data.equals("changePassword")) {
        sendMessage.setText("Porolni o'zgartirish uchun masalan so'z boshida (ChangePassword 123456) usulida kiriting ");
      }

      userFilters.put(calBackId, filter);
      System.out.println(filter);
      execute(sendMessage);

    }
  }

  private InlineKeyboardMarkup getUserAdditionalSettingInlineKeyboard() {
    InlineKeyboardButton button1 = new InlineKeyboardButton();
    button1.setText("Income");
    button1.setCallbackData("filter_type_income");

    InlineKeyboardButton button2 = new InlineKeyboardButton();
    button2.setText("Expense");
    button2.setCallbackData("filter_type_expense");

    // Валюта
    InlineKeyboardButton currencyButton1 = new InlineKeyboardButton();
    currencyButton1.setText("USD");
    currencyButton1.setCallbackData("filter_currency_usd");

    InlineKeyboardButton currencyButton2 = new InlineKeyboardButton();
    currencyButton2.setText("RUB");
    currencyButton2.setCallbackData("filter_currency_rub");

    InlineKeyboardButton currencyButton3 = new InlineKeyboardButton();
    currencyButton3.setText("UZS");
    currencyButton3.setCallbackData("filter_currency_uzs");

    // Период даты
    InlineKeyboardButton dateBtn = new InlineKeyboardButton();
    dateBtn.setText("Tanlang sanani");
    dateBtn.setCallbackData("filter_date");

    InlineKeyboardButton janButton = new InlineKeyboardButton("Январь");
    janButton.setCallbackData("month_january");

    InlineKeyboardButton febButton = new InlineKeyboardButton("Февраль");
    febButton.setCallbackData("month_february");

    InlineKeyboardButton marButton = new InlineKeyboardButton("Март");
    marButton.setCallbackData("month_march");

    InlineKeyboardButton aprButton = new InlineKeyboardButton("Апрель");
    aprButton.setCallbackData("month_april");

    InlineKeyboardButton mayButton = new InlineKeyboardButton("Май");
    mayButton.setCallbackData("month_may");

    InlineKeyboardButton junButton = new InlineKeyboardButton("Июнь");
    junButton.setCallbackData("month_june");

    InlineKeyboardButton julButton = new InlineKeyboardButton("Июль");
    julButton.setCallbackData("month_july");

    InlineKeyboardButton augButton = new InlineKeyboardButton("Август");
    augButton.setCallbackData("month_august");

    InlineKeyboardButton sepButton = new InlineKeyboardButton("Сентябрь");
    sepButton.setCallbackData("month_september");

    InlineKeyboardButton octButton = new InlineKeyboardButton("Октябрь");
    octButton.setCallbackData("month_october");

    InlineKeyboardButton novButton = new InlineKeyboardButton("Ноябрь");
    novButton.setCallbackData("month_november");

    InlineKeyboardButton decButton = new InlineKeyboardButton("Декабрь");
    decButton.setCallbackData("month_december");

    InlineKeyboardButton saveBtn = new InlineKeyboardButton("Tasdiqlash");
    saveBtn.setCallbackData("save");

    InlineKeyboardButton resetBtn = new InlineKeyboardButton("filtrni bo'shatish");
    resetBtn.setCallbackData("reset");


    // Строки кнопок
    List<InlineKeyboardButton> typeRow = new ArrayList<>();
    typeRow.add(button1);
    typeRow.add(button2);

    List<InlineKeyboardButton> currencyRow = new ArrayList<>();
    currencyRow.add(currencyButton1);
    currencyRow.add(currencyButton2);
    currencyRow.add(currencyButton3);

    List<InlineKeyboardButton> dateRow = new ArrayList<>();
    dateRow.add(dateBtn);

    List<InlineKeyboardButton> saveRow = new ArrayList<>();
    saveRow.add(saveBtn);

    List<InlineKeyboardButton> resetRow = new ArrayList<>();
    resetRow.add(resetBtn);

    // Добавляем строки в клавиатуру
    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
    rows.add(typeRow);
    rows.add(currencyRow);
    rows.add(dateRow);
    rows.add(Arrays.asList(janButton, febButton, marButton));
    rows.add(Arrays.asList(aprButton, mayButton, junButton));
    rows.add(Arrays.asList(julButton, augButton, sepButton));
    rows.add(Arrays.asList(octButton, novButton, decButton));
    rows.add(saveRow);
    rows.add(resetRow);

    inlineKeyboard.setKeyboard(rows);
    return inlineKeyboard;
  }


  private boolean validatePassword(String password) {
    try {
      Authentication authenticate = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(selectUser.get().getUsername(), password)
      );
      return authenticate.isAuthenticated();
    } catch (BadCredentialsException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  private boolean checkLoginInDatabase(String login) {
    Optional<User> user = userRepo.findByUsername(login);
    selectUser = user;
    System.out.println(user);
    System.out.println(login);

    return user.isPresent();
  }

  private ReplyKeyboard getSettingKeyboard() {
    KeyboardButton documentButton = new KeyboardButton("Hisobotlar");
    KeyboardButton settingButton = new KeyboardButton("Sozlamalar");


    KeyboardRow row = new KeyboardRow();
    row.add(documentButton);
    row.add(settingButton);

    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    keyboardMarkup.setKeyboard(Collections.singletonList(row));
    keyboardMarkup.setResizeKeyboard(true);
    keyboardMarkup.setOneTimeKeyboard(true);

    return keyboardMarkup;
  }

  private InlineKeyboardMarkup getUserReportPanelInlineKeyboard() {
    InlineKeyboardButton button1 = new InlineKeyboardButton();
    button1.setText("Oylik daromadlar");
    button1.setCallbackData("monthlyIncome");

    InlineKeyboardButton button2 = new InlineKeyboardButton();
    button2.setText("Oylik xarajatlar");
    button2.setCallbackData("monthlyExpense");

    InlineKeyboardButton button3 = new InlineKeyboardButton();
    button3.setText("Qo'shimcha hisobotlar");
    button3.setCallbackData("additionalReport");

    List<InlineKeyboardButton> row = new ArrayList<>();
    row.add(button1);
    row.add(button2);
    row.add(button3);

    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
    inlineKeyboard.setKeyboard(Collections.singletonList(row));

    return inlineKeyboard;
  }

  private InlineKeyboardMarkup getUserSettingPanelInlineKeyboard() {
    InlineKeyboardButton button1 = new InlineKeyboardButton();
    button1.setText("Profil malumotlarni o'zgartirish");
    button1.setCallbackData("changeProfile");

    InlineKeyboardButton button2 = new InlineKeyboardButton();
    button2.setText("Kirish huquqini ko'rish");
    button2.setCallbackData("viewAccess");

    InlineKeyboardButton button3 = new InlineKeyboardButton();
    button3.setText("Parolni o'zgartirish");
    button3.setCallbackData("changePassword");

    List<InlineKeyboardButton> row = new ArrayList<>();
    row.add(button1);
    row.add(button2);
    row.add(button3);

    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
    inlineKeyboard.setKeyboard(Collections.singletonList(row));

    return inlineKeyboard;
  }

  private ByteArrayInputStream generateExcelIncome(List<MonthlyIncomeProjection> monthlyIncome) {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Monthly Income");

      Row headerRow = sheet.createRow(0);
      String[] headers = {"ID", "Month", "Status", "Amount", "Currency", "Amount in UZS", "Description", "Client Name", "Type"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      int rowIndex = 1;
      for (MonthlyIncomeProjection income : monthlyIncome) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(income.getId());
        row.createCell(1).setCellValue(income.getMonth());
        row.createCell(2).setCellValue(income.getStatus());
        row.createCell(3).setCellValue(income.getAmount());
        row.createCell(4).setCellValue(income.getCurrency());
        row.createCell(5).setCellValue(income.getAmountInuzs());
        row.createCell(6).setCellValue(income.getDescription());
        row.createCell(7).setCellValue(income.getClientName());
        row.createCell(8).setCellValue(income.getType());
      }


      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private ByteArrayInputStream generateExcelExpense(List<MonthlyExpensProjection> monthlyExpense) {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Monthly Income");

      Row headerRow = sheet.createRow(0);
      String[] headers = {"ID", "Month", "Amount", "Currency", "Amount in UZS", "Description", "Type", "Name"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      int rowIndex = 1;
      for (MonthlyExpensProjection income : monthlyExpense) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(income.getId());
        row.createCell(1).setCellValue(income.getMonth());
        row.createCell(2).setCellValue(income.getAmount());
        row.createCell(3).setCellValue(income.getCurrency());
        row.createCell(4).setCellValue(income.getAmountInuzs());
        row.createCell(5).setCellValue(income.getDescription());
        row.createCell(6).setCellValue(income.getType());
        row.createCell(7).setCellValue(income.getName());
      }

      // Сохранение в поток
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private ByteArrayInputStream generateExcelFilter(List<GetTransactionBySelectedSetting> monthlyExpense) {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("selected filters");

      Row headerRow = sheet.createRow(0);
      String[] headers = {"TransactionDate", "Type", "Status", "Amount", "Currency", "Amount in UZS", "Description", "Category", "ClientName", "ServiceType"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      int rowIndex = 1;
      for (GetTransactionBySelectedSetting income : monthlyExpense) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(income.getTransactionDate());
        row.createCell(1).setCellValue(income.getType());
        row.createCell(2).setCellValue(income.getStatus());
        row.createCell(3).setCellValue(income.getAmount());
        row.createCell(4).setCellValue(income.getCurrency());
        row.createCell(5).setCellValue(income.getAmountInuzs());
        row.createCell(6).setCellValue(income.getDescription());
        row.createCell(7).setCellValue(income.getCategory());
        row.createCell(8).setCellValue(income.getClientname());
        row.createCell(9).setCellValue(income.getServicetype());
      }


      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      workbook.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void sendExcelToUser(Long chatId, ByteArrayInputStream excelStream, String formatName) {
    SendDocument sendDocument = new SendDocument();
    sendDocument.setChatId(chatId);

    InputFile excelFile = new InputFile();
    excelFile.setMedia(excelStream, formatName);
    sendDocument.setDocument(excelFile);

    try {
      execute(sendDocument);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private InlineKeyboardMarkup getUserNameOrUserFullNameInlineKeyboard() {
    InlineKeyboardButton button1 = new InlineKeyboardButton("Logini o'zgartitish");
    button1.setCallbackData("updateLogin");

    InlineKeyboardButton button2 = new InlineKeyboardButton("Ismni o'zgartirish");
    button2.setCallbackData("updateName");


    List<InlineKeyboardButton> row = new ArrayList<>();
    row.add(button1);
    row.add(button2);


    InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
    inlineKeyboard.setKeyboard(Collections.singletonList(row));

    return inlineKeyboard;
  }


}

