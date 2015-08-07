package com.poshnyamane.pattern_tracking;

/**
 * Created by poshnyamane on 8/3/15.
 */

import android.app.Activity;
import org.opencv.highgui.Highgui;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import android.os.Environment;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Point;
import org.opencv.core.Scalar;



public class TemplateMatching extends Activity {

    String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

    //match the template
    public void match() {

        //fetch the template picture and the current camera frame as Matrices of points
        Mat templ = Highgui.imread(baseDir + "/Test/template.png");
        Mat img = Highgui.imread(baseDir+ "/Test/pic.png");


        //Create a Mat for the result picture
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);



        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        MinMaxLocResult minmaxResult = Core.minMaxLoc(result);
        Point matchLoc;

        if (Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF || Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = minmaxResult.minLoc;
        } else {
            matchLoc = minmaxResult.maxLoc;
        }

        Core.rectangle(
                img,
                matchLoc,
                new Point(matchLoc.x + templ.cols(), matchLoc.y
                        + templ.rows()), new Scalar(180, 255, 0));

        Highgui.imwrite(baseDir + "/Test/result.png", img);

    }

}
