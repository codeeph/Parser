import java.util.Scanner;

public class Program {
    public static void main(String [] args)
    {
        Scanner scan = new Scanner(System.in);
        Parser parser = new Parser();
        while (true)
        {
            System.out.printf("Выберите действие:\n<1> - Загрузить страницу.\n<2> - Получить ссылки страницы.\n" +
                    "<3> - Получить актуальные цены криптовалют.\n<4> - Получить актуальные новости.\n");
            String choise = scan.next();
            switch (choise)
            {
                case "1":
                {
                    System.out.printf("Введите ссылку страницы:\n");
                    String url = scan.next();
                    parser.LoadDocument(url);
                    break;
                }
                case "2":
                {
                    parser.GetRefs();
                    break;
                }
                case "3":
                {
                    parser.GetPricesCrypto();
                    break;
                }
                case "4":
                {
                    parser.GetNews();
                    break;
                }
                default:
                {
                    System.out.printf("Данного действия не существует!\n");
                    break;
                }
            }

        }
    }
}
