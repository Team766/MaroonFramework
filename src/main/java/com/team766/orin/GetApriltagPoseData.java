package com.team766.orin;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import java.util.ArrayList;

public class GetApriltagPoseData {

    public static ArrayList<AprilTag> getAllTags() {
        ArrayList<AprilTag> apriltags = new ArrayList<AprilTag>();

        int[] tagIds;
        double[] tagData;

        try {
            tagIds = GetOrinRawValue.getIntArray("tag_id");
            tagData = GetOrinRawValue.getDoubleArray("raw_pose");
        } catch (ValueNotFoundOnTableError e) {
            return apriltags; // Can just return an array of zero apriltags here
        }

        ArrayList<Double> data = new ArrayList<Double>();
        for (double x : tagData) data.add(x);

        for (int i = 0; i < tagIds.length; i++) {
            AprilTag tag =
                    new AprilTag(
                            tagIds[i],
                            new Pose3d(
                                    new Translation3d(
                                            data.remove(0), data.remove(0), data.remove(0)),
                                    new Rotation3d()));
            // Remove used values from array

            apriltags.add(tag);
        }
        return apriltags;
    }
}
