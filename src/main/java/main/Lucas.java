package main;

import java.io.File;
import java.io.FileReader;

import model.Movie;
import service.Parser;
import util.UiUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import exception.ParseException;

public class Lucas {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args);

            Gson gson = new Gson();
            FileReader reader = new FileReader((File) parser.get(Parser.PATH));

            Movie[] movies = gson.fromJson(reader, Movie[].class);

            UiUtils.showMessage("Movies loaded:");
            for (Movie movie : movies) {
                UiUtils.showMessage(movie.Title);
            }


            //TODO handle each query
        } catch (ParseException e) {
            UiUtils.showError(e.getMessage());
        } catch (JsonSyntaxException e) {
            UiUtils.showError("The Json structure of the file isn't correct");
        } catch (Exception e) {
            UiUtils.showError("There was an error while loading the file");
        }
    }
}
