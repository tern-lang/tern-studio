package org.ternlang.studio.common.find.text;

import java.util.List;

public interface TextMatchListener {
   void onMatch(TextFile file, List<TextMatch> matches);
   void onError(TextFile file, Exception cause);
}