# MISEX bot
Bot provides information about Moscow stocks status. Moscow stock exchange has API that provides access to all stock's information.
It available on https://iss.moex.com/iss/reference/
Documentation on http://fs.moex.com/files/6523

It realizes following functionality:
1. Searching stocks by key word.
2. Show detailed information of one stock.
3. Managing favorite stocks list.

Bot's available options:

1. /start - shows all available options.
2. /search {key_word} - shows list of stocks, that have key_word in ISIN code, name or description of stock.
3. /info {isin} - shows detailed information of stock.
4. /list - shows list of favorite stocks.
5. /add {isin} - adds new stock to favorites list.
6. /delete {isin} - remove stock from favorites list.

### Examples:
#### Search
Request: /search Сбербанк
Response:
1. SBER - Сбербанк ОАО ао
2. SBERP - Сбербанк ОАО ап

This query returns list of 2 stocks.
#### Info
Request: /info SBER
Response:
![alt text](https://github.com/Kroca/Scala-bot/blob/dev/example.png "Example")

SBER - Сбербанк
 
Открытие: 224.4

Текущая цена: 223.73

Изменение: -0,30%

###Contacts

Telegram bot: @MISEX_stock_bot

Admin: @almazmelnikov

Contact us, AlphaTeam.