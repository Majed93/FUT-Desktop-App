package com.fut.desktop.app.restObjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fut.desktop.app.parameters.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonFormat
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchBody {

    /**
     * Id of account
     */
    Integer id;

    /**
     * The lists used
     */
    List<String> list;

    /**
     * List of players
     */
    List<Player> players;

    /**
     * Level to search (optional)
     */
    Level level;
}
