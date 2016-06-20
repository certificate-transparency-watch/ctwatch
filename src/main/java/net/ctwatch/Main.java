package net.ctwatch;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import net.ctwatch.db.Db;
import net.ctwatch.http.ApiResource;
import net.ctwatch.job.SyncLogEntries;
import net.ctwatch.logServerApi.LogServerService;
import net.ctwatch.utils.GuavaUtils;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Main extends Application<CtWatchConfiguration> {
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    public void run(CtWatchConfiguration configuration, Environment environment) throws Exception {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ct.googleapis.com/aviator/")
                .addConverterFactory(JacksonConverterFactory.create())
                .validateEagerly(true)
                .build();
        Db db = new Db();
        ApiResource apiResource = new ApiResource(db);
        LogServerService logServerService = retrofit.create(LogServerService.class);
        environment.lifecycle().manage(GuavaUtils.fromService(new SyncLogEntries(logServerService, db)));
        environment.jersey().register(apiResource);
    }

    @Override
    public String getName() {
        return "ct-watch";
    }
}
