# Утилита фильтрации содержимого файлов
## Реализация

* Java(TM) SE Runtime Environment (build 22.0.1+8-16)
* Apache Maven 4.0.0

Подключенные зависимости

* Apache commons-cli 1.9.0
* lombok 1.18.34
* junit-jupiter-api 5.11.0
* Apache maven-assembly-plugin 3.7.0

[Перейти к pom.xml](./pom.xml)

## Описание
### Запуск утилиты
Запуск утилиты осуществляется:
```shell
java -jar filtering-utility-[version].jar [options]
```
где:  
[version] - версия,  
[options] - список параметров 

Посмотреть все доступные параметры можно передав в командной строке параметр `-h` или `--help`.  
Пример запуска с опцией `-h`: 

```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -h
usage: java -jar filtering-utility-[VERSION].jar [options] -if file_01 file_02 ...
 -a,--add-result             add result to exist files
 -f,--full                   full report
 -h,--help                   show this help
 -if,--input-files <FILES>   input file names separated by space as
                             'file_01 file_02 ...'
 -o,--output-path <PATH>     output result path
 -p,--prefix <PREFIX>        output file names prefix
 -s,--short                  short report
```

При запуске утилиты с непредусмотренной опцией, опция будет проигнорирована, утилита продолжит выполнение
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -wrong -o ./results -f -if ./sample_files/in1.txt 
Игнорируем неопознанный аргумент командной строки: Unrecognized option: -wrong
```
### Результат работы. Вывод статистики
Результат работы - создание файлов результатов, каждый из которых содержит данные одного типа (целые, 
вещественные числа, строки).  
По умолчанию имена файлов определены как `integers.txt`, `floats.txt`, `strings.txt`. Также размещение файлов 
по умолчанию определено в текущей папке.  
Добавить к указанным именам префикс, а также указать иное расположение для сохранения файлов можно 
соответственно задав параметры `-p`/`--prefix` и `-o`/`--output-path`.  
Опцией `-a`/`--add-result` можно установить режим добавления. В таком случае, если указанные файлы результатов 
существуют, они не будут перезаписаны и результаты будут добавлены в существующие файлы.

По завершении работы утилиты можно получить статистику по обработанным данным. Установкой параметра `-s`/`--short` 
будет отображена краткая информация:

```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -s -if ./sample_files/in1.txt ./sample_files/in2.txt
  
  Статистика выполнения
======================================
* для целых чисел:
Количество записанных элементов: 3
--------------------------------------
* для вещественных чисел:
Количество записанных элементов: 3
--------------------------------------
* для строк:
Количество записанных элементов: 6
--------------------------------------
```
При запуске с параметром `-f`/`--full` будет выведена подробная статистика:

```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -f -if ./sample_files/in1.txt ./sample_files/in2.txt
  
  Статистика выполнения
======================================
* для целых чисел:
Количество записанных элементов: 3
Минимальное значение: 45
Максимальное значение: 1234567890123456789
Сумма элементов: 1234567890123557334
Среднее значение: 411522630041185778
--------------------------------------
* для вещественных чисел:
Количество записанных элементов: 3
Минимальное значение: -0.001
Максимальное значение: 3.1415
Сумма элементов: 3.1405
Среднее значение: 1.0468334
--------------------------------------
* для строк:
Количество записанных элементов: 6
Размер минимальной строки: 4
Размер максимальной строки: 42
--------------------------------------
```
При указании одновременно обоих параметров `-s` и `-f` приоритет остается за подробным выводом.

## Особенности реализации
### Чтение/запись
Заданием предусмотрена поочередное чтение исходных файлов, при этом заранее их количество не определено. 
В данной реализации утилиты предусмотрено создание отдельных "производителей" `FileProducer` для чтения каждого из 
переданных утилите файлов запускаемых в отдельных потоках (Thread).

`Filter.java`:
```java

List<Thread> producerThreads = new ArrayList<>();
        parameters.getFileList()
                .forEach(file -> {
                    producerThreads.add(new Thread(new FileProducer(integersQueue, floatsQueue, stringsQueue, file)));
                });
```
Данные, прочитанные производителем передаются в соответствующую типу данных очередь реализованную при помощи 
`LinkedBlockingQueue`:

`FileProducer.java`
```java
public void run() {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            destinationQueue(line);
        }
    } catch (IOException exception) {
        System.err.println("Ошибка при чтении файла: " + exception.getMessage() +
                ". Данные файла не будут обработаны");
    }
}
```
Откуда (из очереди) "потребители" получают данные для записи в файлы результатов.  
Также по условию задания, если в исходных файлах отсутствуют данные определенного типа, 
результирующий файл не создается.

`FileConsumer.java`
```java
public void run() {
    try {
        String line;
            /*
             Ожидаем первый элемент в очереди.
             Если не поступил, файл результатов для данного типа не создается
             */
        if ((line = queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, appendIfFileExist))) {
                while (line != null) {
                    bw.write(line);
                    bw.newLine();
                    setStatistics(line);
                    line = queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                }
            } catch (IOException exception) {
                System.err.println("Ошибка при сохранении в файл: " + exception.getMessage() +
                        ". Данные не будут добавлены в файл результатов");
            }
        }
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```
### Обработка исключений
Утилитой предусмотрена обработка исключений. При этом возможно частичная обработка при наличии ошибок.
#### Полный останов программы
Полный останов программы выполняется в следующих случаях:
* в командной строке не указаны имена файлов-источников данных:
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -f

Не заданы файлы с исходными данными. Дальнейшее выполнение невозможно
```
* все указанные параметрами файлы некорректны
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -f -if wrong_file.txt

Указанные файлы входных данных отсутствуют. Дальнейшее выполнение невозможно
```
#### Частичное выполнение программы
Частичное выполнение возможно в следующих случаях:
* В командной строке передан некорректный аргумент. В данном случае аргумент будет проигнорирован, 
программа продолжит выполнение:
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -wrong -o ./results -f -if ./sample_files/in1.txt 
Игнорируем неопознанный аргумент командной строки: Unrecognized option: -wrong
```
* Имя одного/нескольких переданных файлов некорректно (при этом хотя бы один файл в списке корректен):
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -if wrong_file.txt ./sample_files/in1.txt

Ошибка при указании файла: wrong_file.txt. Файл исключен из обработки
```
* Ошибка ввода/вывода при сохранении в указанный файл результатов. При этом для остальных типов данные будут 
обработаны
```shell
java -jar filtering-utility-1.0-jar-with-dependencies.jar -s -if ./sample_files/in1.txt

Ошибка при сохранении в файл: .\results\prefix_floats.txt (Отказано в доступе). 
Данные не будут добавлены в файл результатов

  Статистика выполнения
======================================
* для целых чисел:
Количество записанных элементов: 2
--------------------------------------
* для вещественных чисел:
Отсутствуют данные указанного типа
--------------------------------------
* для строк:
Количество записанных элементов: 4
--------------------------------------
```
### Сбор статистики
Сбор статистики выполнен с использованием интерфейса `PropertyChangeListener` реализованного в классе 
`StatisticsListener.java`.

Уведомление об изменении параметра "наблюдателю" выполняется после удачной записи в файл результата
`FileConsumer.java`
```java
try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, appendIfFileExist))) {
    while (line != null) {
        bw.write(line);
        bw.newLine();
        // Изменение параметра
        setStatistics(line);
        line = queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
    }
```
Далее "наблюдатель" отправляет полученные данные для расчета соответствующему экземпляру `CommonStatistics` 