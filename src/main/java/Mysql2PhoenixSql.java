import java.io.IOException;

/**
 * Created by peichenchen on 17/9/29.
 */
public class Mysql2PhoenixSql {

    public static void main(String[] args) throws IOException {
        new SingleTableConvertor().convert("/Users/peichenchen/Downloads/temp/testSql", "DAIJIA_ORDER", 64);
    }

}
