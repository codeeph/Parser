import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
public class Parser {
    public final String URL_CRYPTO = "https://myfin.by/crypto-rates";
    public final String URL_NEWS = "https://ria.ru/";
    public  final String URL_QIWI = "https://edge.qiwi.com/sinap/crossRates";
    public final String URL_HELPIX = "https://helpix.ru/currency/";

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
        Document doc = GetDocument(URL_CRYPTO);
        if(doc == null)
            return;
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
        Document doc = GetDocument(URL_NEWS);
        if(doc == null)
            return;
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
    public Document GetDocument(String URL)
    {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (Exception e) {
            System.out.printf("Произошла непредвиденная ошибка при загрузке страницы(%s):\n%s\n",URL,e.getMessage());
            return null;
        }
        return doc;
    }
    public void GetPricesUSD()
    {
        Document documentHelpix = GetDocument(URL_HELPIX);
        byte[] bytes = null;
        try {
             bytes = readParse(URL_QIWI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String jsonQiwi = new String(bytes, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(jsonQiwi);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONArray jsAr = (JSONArray) json.get("result");
        JSONObject ObjectUSDQiwi = null;
        for (int i = 0; i < jsAr.size(); i++)
        {
            if(jsAr.get(i).toString().contains("643") && jsAr.get(i).toString().contains("840")) {
                ObjectUSDQiwi = (JSONObject) jsAr.get(i);
                break;
            }
        }


        String USDQiwi = ObjectUSDQiwi.get("rate").toString();
        System.out.println(USDQiwi);
        if (documentHelpix == null)
            return;
        Elements tdElements = documentHelpix.select("td");

        String USDCB = tdElements.get(8).text();
        String USDAli = tdElements.get(9).text();

        System.out.printf("CB  \tAli \tQiwi\n%s\t%s\t%s\n",USDCB,USDAli,USDQiwi);

    }
    public byte[] readParse(String urlPath) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();

        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);

        }
        inStream.close();
        return outStream.toByteArray();

    }
}