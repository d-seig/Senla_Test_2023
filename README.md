# Senla_Test_2023

Тестовое приложение на стажировку в Senla

Приложение для симуляции работы банкомата в консоли.

Для запуска необходимо скачать .jar и .bat и запустить последний.
Карты, которые пропустит прога, лежат в C:/SENLA_STAGE/cards.txt, файлик создастся сам. 
Туда можно дописывать новые значения в таком же формате. Иначе программа будет ругаться.

Приложение начинает свою работу с инициализации текстового интерфейса. Выбор действий осуществляется вводом цифр:
[1] Вставить карту
[2] Инкассация (взятие)
[3] Инкассация (пополнение)
[4] Открыть банкомат (логи)
[0] Завершение работы

При выборе 1 - открывается интерфейс для операций с картой:
[1] Узнать баланс (остаток на счете)
[2] Снять наличные
[3] Пополнить баланс
[4] Перевести на другой счет
[0] Выход

При выборе 1 - выводит количество денег на счете
При выборе 2 - тоже самое что и ниже, только со списанием. Если денег недостаточно на счете или в банкомате - выводит предупреждение об этом
При выборе 3 - происходит зачисление на банковский счет указанной суммы, занесение денег в банкомат, добавление записи в логи и изменение баланса в файле с картами.
Если денег внесено более 1 млн - выводит предупреждение.
При выборе 4 - ничего не происходит (:
При выборе 0 - возврат к меню банкомата

В меню банкомата:
При выборе 2 - требует ключ, позволяет забирать деньги из банкомата
При выборе 3 - требует ключ, позволяет ложить деньги в банкомат
При выборе 4 - требует ключ, позволяет просматривать логи и данные банковских карт
При выборе 0 - завершение работы программы.

Ключ для доступа к деньгам и данным - 34Y59F8.

Для хранения данных используются текстовые файлы, которые, в случае их отсутствия, генерируются в дирректории C:/SENLA_STAGE.
Файлы cards.txt и logs.txt, соответственно. В cards.txt, при создании, записываются значения для примера.

Для оптимизации чтения из файлов использовано хранение значений из файла в коллекции cacheCards. 
Обновление "кеша" происходит при записи туда новых значений через методы класса DataBase.

При введении неправильно пинкода 3 раза - карта блокируется на сутки. В этом состоянии она не может совершать действия из пунктов 2, 3, 4.

Для зачисления средств в банкомат используется интерфейс CashInterface.

Общая структура проекта:
CashMashine - банкомат и все что он из себя представляет
Card - карточка и предоставляемый ей интерфейс. По факту, содержит в себе ссылки на банкомат и счет (для обеспечения связи с их функциями),
  так что в некотором роде - заместитель банковского счета.
CashInterface - используется для зачисления средств в банкомат при операциях с картой
BankAccount - счет и все что он из себя представляет. Содержит регулярку для проверки правильности номера карты
DataBase - класс для работы с файлами и "кэшем". Содержит функции записи логов и значений карт, создание файлов, папки,
  а также метод для проверки правильности введенных в ФАЙЛ значений.
