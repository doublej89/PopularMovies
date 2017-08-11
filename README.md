# PopularMovies

This is the final version of the popular movies app for Udacity's Android Developer Nanodegree program.

Launching the app opens a page that displays a grid of the most popular movies' poster images.

Through the settings (The settings option is available only in the main page), the posters in the grid
can be sorted according to three different sorting orders: By Popularity, By Top Rated, 
and By Favorites.

Clicking/tapping on any poster opens a details page for that movie which shows the movies name
release date, vote average, overview, poster, trailers and reviews.
Clicking/tapping on the play button image of a particular trailer opens a corresponding video
of the trailer.

The details page also has a favorites button clicking/tapping which marks that movie as the
user's favorite. Clicking/tapping the button again undoes the movie's favorites status. Sorting
the main page (posters grid) according to the favorites option displays the posters of all the
movies that have been marked as favorite.

# MovieDB API Key

You will need your moviedb api key to allow the app to fetch movie related data from the movie database. 
Create the file .gradle\gradle.properties in your android projects folder (if one doesn't exist already)
and add the line MyMovieDBApiKey = "Your_api_key" to it.

# Installation

Just clone the repo, 
