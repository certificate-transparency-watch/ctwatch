package net.ctwatch.logServerApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LogServerService {

    @GET("ct/v1/get-entries")
    Call<GetEntries> getEntries(@Query("start") int start, @Query("end") int end);

}
