package com.example.labo4


import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cardview_movie.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var movieList:ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        initSearchBar()
    }

    fun initSearchBar() = add_movie_btn.setOnClickListener{
        if(!movie_name_et.text.isEmpty()){
            FetchMovie().execute(movie_name_et.text.toString())
        }
    }

    fun initRecyclerView(){
        viewManager = LinearLayoutManager(this)
        movieAdapter = MovieAdapter(movieList)

        movie_list_rv.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = movieAdapter
        }
    }

    fun addMovieToList(movie: Movie){
        movieList.add(movie)
        movieAdapter.changeList(movieList)
        Log.d("Number", movieList.size.toString())
    }

    private inner class FetchMovie: AsyncTask<String, Void, String>()
    {
        override fun doInBackground(vararg params: String): String {
            if(params.isNullOrEmpty())return "";

            val movieName:String = params [0];
            val movieURL: URL = NetworkUtils().buildSearchUrl(movieName)
            return try {
                NetworkUtils().getResponseFromHttpUrl(movieURL)
            }catch (e: IOException){
                ""
            }
        }

        override fun onPostExecute(movieInfo: String) {
            super.onPostExecute(movieInfo)

            if(!movieInfo.isEmpty())
            {
                val movieJson = JSONObject(movieInfo)
                if(movieJson.getString("Response")=="True")
                {
                    val movie = Gson().fromJson<Movie>(movieInfo, Movie::class.java)
                    addMovieToList(movie)
                }
                else
                {
                    Snackbar.make(main_ll,"No existe la movie", Snackbar.LENGTH_LONG).show();
                }
            }
        }

    }

}
