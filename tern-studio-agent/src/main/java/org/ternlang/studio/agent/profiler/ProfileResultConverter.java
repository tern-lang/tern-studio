package org.ternlang.studio.agent.profiler;

import org.ternlang.agent.message.common.ProfileResultArray;
import org.ternlang.agent.message.common.ProfileResultArrayCodec;
import org.ternlang.message.ByteArrayFrame;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProfileResultConverter {

    public static Set<ProfileResult> convert(ProfileResultArray results) {
        Set<ProfileResult> set = new LinkedHashSet<ProfileResult>();

        for(org.ternlang.agent.message.common.ProfileResult result : results) {
            ProfileResult value = new ProfileResult(
                    result.resource().toString(),
                    result.time(),
                    result.count(),
                    result.line());

            set.add(value);
        }
        return set;
    }

    public static ProfileResultArray convert(Set<ProfileResult> results) {
        ByteArrayFrame frame = new ByteArrayFrame();
        ProfileResultArrayCodec codec = new ProfileResultArrayCodec(Integer.MAX_VALUE);

        codec.with(frame, 0, Integer.MAX_VALUE);
        frame.setCount(8);

        for(ProfileResult result : results) {
            codec.add()
                .count(result.getCount())
                .line(result.getLine())
                .resource(result.getResource())
                .time(result.getTime());
        }
        return codec;
    }
}
