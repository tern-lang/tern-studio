package tern.studio.common.find;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchPart {
   
   private final String begin;
   private final String match;
}