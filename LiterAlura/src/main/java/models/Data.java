package models;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
public record Data(
        @JsonAlias("results") List<BookData> result) {

}
