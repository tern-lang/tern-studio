package org.ternlang.studio.common.find.text;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextMatchResult {

   private final TextFile file;
   private final List<TextMatch> matches;
}