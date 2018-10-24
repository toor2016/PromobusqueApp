package promobusque.ramon.promobusqueapp.retrofit

import promobusque.ramon.promobusqueapp.servicosretrofit.EmpresaService
import promobusque.ramon.promobusqueapp.servicosretrofit.PromocaoService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInitializer {

    //val url = "http://192.168.1.3:8000"
    val url = "http://172.16.161.140:8000"
    private val retrofit = Retrofit.Builder()
                                    .baseUrl(url)
                                    .addConverterFactory(MoshiConverterFactory.create())
                                    .build()

    fun empresaService(): EmpresaService = retrofit.create(EmpresaService::class.java)
    fun promocaoService(): PromocaoService = retrofit.create(PromocaoService::class.java)
}