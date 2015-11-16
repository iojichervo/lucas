# Lucas

Lucas is a hazelcast client designed for making map reduce queries over movies with the JSONs obtained from [OMDb API](http://www.omdbapi.com/). These queries are:

1. The N most popular actors (Popularity is measured by the IMDB votes)
2. For each year, greater than the year *tope*, the most acclaimed movie (Based on the Metascore)
3. The pairs of actors that appeared on more movies, and for each, the list of the movies
4. For each director, the fetish actors, meaning the actors that appeared on more movies of the director

## Installation

```
./gradlew shadowJar
```

This command will produce an executable jar in the `./build/libs/` directory.

## Usage

You can run an instance easily running

```
./src/main/resources/instance.sh
```

And each query can be run with

```
java -Daddresses=(ADDRESS OF A NODE) -jar ./build/libs/(JAR) query=(QUERY NUMBER) [n=(NUMBERS OF ACTORS)|tope=(MINIMUM YEAR)] path=(PATH TO A JSON)
```

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## License

Lucas is available under the GNU General Public License v2.0 License.