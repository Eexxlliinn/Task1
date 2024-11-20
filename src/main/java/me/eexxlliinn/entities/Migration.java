package me.eexxlliinn.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Migration {

    private final String version;
    private final String file;
    private final String rollbackFile;
}
