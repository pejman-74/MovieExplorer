package com.movie_explorer.utils

import com.google.gson.Gson
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail


const val dummySuccessApiResponse = "{\n" +
        "  \"data\": [\n" +
        "    {\n" +
        "      \"id\": 1,\n" +
        "      \"title\": \"The Shawshank Redemption\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0111161_poster.jpg\",\n" +
        "      \"year\": \"1994\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"9.3\",\n" +
        "      \"genres\": [\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0111161_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0111161_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0111161_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 2,\n" +
        "      \"title\": \"The Godfather\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0068646_poster.jpg\",\n" +
        "      \"year\": \"1972\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"9.2\",\n" +
        "      \"genres\": [\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0068646_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0068646_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0068646_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 3,\n" +
        "      \"title\": \"The Godfather: Part II\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0071562_poster.jpg\",\n" +
        "      \"year\": \"1974\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"9.0\",\n" +
        "      \"genres\": [\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0071562_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0071562_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0071562_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 4,\n" +
        "      \"title\": \"The Dark Knight\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0468569_poster.jpg\",\n" +
        "      \"year\": \"2008\",\n" +
        "      \"country\": \"USA, UK\",\n" +
        "      \"imdb_rating\": \"9.0\",\n" +
        "      \"genres\": [\n" +
        "        \"Action\",\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0468569_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0468569_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0468569_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 5,\n" +
        "      \"title\": \"12 Angry Men\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0050083_poster.jpg\",\n" +
        "      \"year\": \"1957\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"8.9\",\n" +
        "      \"genres\": [\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0050083_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0050083_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0050083_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 6,\n" +
        "      \"title\": \"Schindler's List\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0108052_poster.jpg\",\n" +
        "      \"year\": \"1993\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"8.9\",\n" +
        "      \"genres\": [\n" +
        "        \"Biography\",\n" +
        "        \"Drama\",\n" +
        "        \"History\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0108052_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0108052_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0108052_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 7,\n" +
        "      \"title\": \"Pulp Fiction\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0110912_poster.jpg\",\n" +
        "      \"year\": \"1994\",\n" +
        "      \"country\": \"USA\",\n" +
        "      \"imdb_rating\": \"8.9\",\n" +
        "      \"genres\": [\n" +
        "        \"Crime\",\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0110912_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0110912_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0110912_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 8,\n" +
        "      \"title\": \"The Lord of the Rings: The Return of the King\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0167260_poster.jpg\",\n" +
        "      \"year\": \"2003\",\n" +
        "      \"country\": \"USA, New Zealand\",\n" +
        "      \"imdb_rating\": \"8.9\",\n" +
        "      \"genres\": [\n" +
        "        \"Adventure\",\n" +
        "        \"Drama\",\n" +
        "        \"Fantasy\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0167260_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0167260_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0167260_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 9,\n" +
        "      \"title\": \"The Good, the Bad and the Ugly\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0060196_poster.jpg\",\n" +
        "      \"year\": \"1966\",\n" +
        "      \"country\": \"Italy, Spain, West Germany, USA\",\n" +
        "      \"imdb_rating\": \"8.9\",\n" +
        "      \"genres\": [\n" +
        "        \"Western\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0060196_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0060196_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0060196_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    },\n" +
        "    {\n" +
        "      \"id\": 10,\n" +
        "      \"title\": \"Fight Club\",\n" +
        "      \"poster\": \"http://moviesapi.ir/images/tt0137523_poster.jpg\",\n" +
        "      \"year\": \"1999\",\n" +
        "      \"country\": \"USA, Germany\",\n" +
        "      \"imdb_rating\": \"8.8\",\n" +
        "      \"genres\": [\n" +
        "        \"Drama\"\n" +
        "      ],\n" +
        "      \"images\": [\n" +
        "        \"http://moviesapi.ir/images/tt0137523_screenshot1.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0137523_screenshot2.jpg\",\n" +
        "        \"http://moviesapi.ir/images/tt0137523_screenshot3.jpg\"\n" +
        "      ]\n" +
        "    }\n" +
        "  ],\n" +
        "  \"metadata\": {\n" +
        "    \"current_page\": \"1\",\n" +
        "    \"per_page\": 10,\n" +
        "    \"page_count\": 25,\n" +
        "    \"total_count\": 250\n" +
        "  }\n" +
        "}"

const val dummySuccessGetMovieDetailApiResponse ="{\n" +
        "  \"id\": 1,\n" +
        "  \"title\": \"The Shawshank Redemption\",\n" +
        "  \"poster\": \"http://moviesapi.ir/images/tt0111161_poster.jpg\",\n" +
        "  \"year\": \"1994\",\n" +
        "  \"rated\": \"R\",\n" +
        "  \"released\": \"14 Oct 1994\",\n" +
        "  \"runtime\": \"142 min\",\n" +
        "  \"director\": \"Frank Darabont\",\n" +
        "  \"writer\": \"Stephen King (short story \\\"Rita Hayworth and Shawshank Redemption\\\"), Frank Darabont (screenplay)\",\n" +
        "  \"actors\": \"Tim Robbins, Morgan Freeman, Bob Gunton, William Sadler\",\n" +
        "  \"plot\": \"Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.\",\n" +
        "  \"country\": \"USA\",\n" +
        "  \"awards\": \"Nominated for 7 Oscars. Another 19 wins & 30 nominations.\",\n" +
        "  \"metascore\": \"80\",\n" +
        "  \"imdb_rating\": \"9.3\",\n" +
        "  \"imdb_votes\": \"1,738,596\",\n" +
        "  \"imdb_id\": \"tt0111161\",\n" +
        "  \"type\": \"movie\",\n" +
        "  \"genres\": [\n" +
        "    \"Crime\",\n" +
        "    \"Drama\"\n" +
        "  ],\n" +
        "  \"images\": [\n" +
        "    \"http://moviesapi.ir/images/tt0111161_screenshot1.jpg\",\n" +
        "    \"http://moviesapi.ir/images/tt0111161_screenshot2.jpg\",\n" +
        "    \"http://moviesapi.ir/images/tt0111161_screenshot3.jpg\"\n" +
        "  ]\n" +
        "}"

val dummyMovieApisResponse: MovieApiResponse =
    Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

val dummyGetMovieDetailApiResponse: MovieDetail =
    Gson().fromJson(dummySuccessGetMovieDetailApiResponse, MovieDetail::class.java)