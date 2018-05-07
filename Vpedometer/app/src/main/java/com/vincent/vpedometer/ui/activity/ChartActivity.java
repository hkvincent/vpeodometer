package com.vincent.vpedometer.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.vincent.vpedometer.R;
import com.vincent.vpedometer.pojo.ChartData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2018/2/13 17:41
 */
public class ChartActivity extends FragmentActivity {


    private View mMainView;
    private LineChartView mLineChartView;
    private ChartData chartData;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        init();
    }


    public void init() {
        initView();
        initData();

    }

    private void initData() {
        Intent intent = getIntent();
        Serializable se = intent.getSerializableExtra("chartData");
        ChartData chartData = null;
        if (se instanceof ChartData) {
            chartData = (ChartData) se;
        }
        this.chartData = chartData;
        getAxisXLables();//corresponding x name or label
        getAxisPoints();//the y name
        initLineChart();//render the chart
    }

    private void initView() {
        mLineChartView = (LineChartView) findViewById(R.id.linechart);
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        //axisX.setMaxLabelChars(5); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setMaxLabelChars(6);
        axisY.setName("steps");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        mLineChartView.setInteractive(true);
        mLineChartView.setZoomType(ZoomType.HORIZONTAL);
        mLineChartView.setMaxZoom((float) 2);//最大方法比例
        mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mLineChartView.setLineChartData(data);
        mLineChartView.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        mLineChartView.setCurrentViewport(v);


    }


    /**
     * get the steps
     */
    private void getAxisPoints() {
        mPointValues.clear();
        ArrayList<String> tempArray = new ArrayList<String>();
        for (int i = 0; i < chartData.getStepData().size(); i++) {
            tempArray.add(chartData.getStepData().get(i));
        }

        for (int i = 0; i < tempArray.size(); i++) {
            mPointValues.add(new PointValue(i, Float.parseFloat(tempArray.get(i))));
        }

    }


    /**
     * get the date
     */
    public void getAxisXLables() {
        mAxisXValues.clear();
        for (int i = 0; i < chartData.getTimeData().size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(chartData.getTimeData().get(i)));

        }

    }

}
