import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public final String URL_CRYPTO = "https://myfin.by/crypto-rates";
    public final String URL_NEWS = "https://ria.ru/";
    public final String URL_WEATHER = "https://www.gismeteo.ru/catalog/russia/";
    public String title;
    private String Url = "";
    private Document document;
    public void LoadDocument(String url)
    {
        if(url.isEmpty())
        {
            System.out.printf("Введенная ссылка пуста!\n");
            return;
        }
        try {
            document = Jsoup.connect(url).get();
            Element titleElement = document.selectFirst("title");
            title = titleElement.text();
            System.out.printf("Страница успешно загружена!\tЕё заголовок - <%s>\n",titleElement.text());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void GetRefs()
    {
        if(document == null)
        {
            System.out.printf("Вы не загрузили страницу!\n");
            return;
        }

        Elements refsEl = document.select("a");
        String[] refsAll = new String[refsEl.size()];
        ArrayList<String> refsList = new ArrayList<String>();

        for (int i = 0; i < refsEl.size(); i++)
            refsAll[i] = refsEl.get(i).attr("href");

        for (int i = 0; i < refsAll.length; i++)
            if(refsAll[i].contains("https://"))
                refsList.add(refsAll[i]);

        String[] refs = new String[refsList.size()];
        for (int i = 0; i < refsList.size(); i++)
            refs[i] = refsList.get(i);


        System.out.printf("Ссылки страницы с заголовком - <%s>:\n", title);
        for (int i = 0; i < refs.length; i++)
            System.out.printf("%d.%s\n",i,refs[i]);
    }
    public void GetPricesCrypto()
    {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL_CRYPTO).get();
        } catch (Exception e) {
            System.out.printf("Произошла непредвиденная ошибка при загрузке страницы:\n%s\n",e.getMessage());
            return;
        }
        Elements tdElements = doc.select("td");
        Elements refElements = doc.select("a");
        ArrayList<String> prices = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < tdElements.size(); i++)
            if(tdElements.get(i).text().contains("$") & tdElements.get(i).className() == "")
                prices.add(tdElements.get(i).text().split(" ")[0]);
        System.out.println(refElements.size());
        for (int i = 0; i < refElements.size(); i++)
            if(refElements.get(i).hasClass("s-bold"))
                names.add(refElements.get(i).text());

        System.out.printf("Актуальные цены криптовалют с сайта - <%s>:\n",URL_CRYPTO);
        for (int i = 0; i < prices.size(); i++)
            System.out.printf("%s  -  %s\n",names.get(i),prices.get(i));
    }
    public void GetNews()
    {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL_NEWS).get();
        } catch (Exception e) {
            System.out.printf("Произошла непредвиденная ошибка при загрузке страницы:\n%s\n",e.getMessage());
            return;
        }
        Elements refElements = doc.select("a");
        ArrayList<String> titlesNews = new ArrayList<String>();
        ArrayList<String> refNews = new ArrayList<String>();
        for (int i = 0; i < refElements.size(); i++)
        {
            if (refElements.get(i).hasClass("cell-list__item-link color-font-hover-only"))
            {
                refNews.add(refElements.get(i).attr("href"));
                titlesNews.add(refElements.get(i).attr("title"));
            }
        }
        System.out.printf("Новости с сайта - <%s>:\n",URL_NEWS);
        for(int i = 0; i < titlesNews.size(); i++)
            System.out.printf("%d.Заголовок - <%s>.\tСсылка на новость - <%s>\n",i,titlesNews.get(i),refNews.get(i));
    }
}