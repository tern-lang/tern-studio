package org.ternlang.studio.common.find.text;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class TextMatchResultCollector implements TextMatchListener {
   
   private final BlockingQueue<TextMatchResult> results;
   private final CountDownLatch latch;
   
   public TextMatchResultCollector(BlockingQueue<TextMatchResult> results, CountDownLatch latch) {
      this.results = results;
      this.latch = latch;
   }

   @Override
   public void onMatch(TextFile file, List<TextMatch> matches) {
      TextMatchResult result = new TextMatchResult(file, matches);
      results.offer(result);
      latch.countDown();
   }

   @Override
   public void onError(TextFile file, Exception cause) {
      latch.countDown();
   }

}