# Test9Unlock

效果图
![](https://github.com/dingqiqi/Test9Unlock/tree/master/gif/a.gif)

重要代码
       
        
        mList = new ArrayList<>();
        mCircleList = new ArrayList<>();
        mSelectList = new ArrayList<>();
        //初始化九个圆均未选中
        for (int i = 0; i < 9; i++) {
            mSelectList.add(false);
        }

        //单个圆圈所处区域高度宽度
        int height = getMeasuredHeight() / 3 * 2 / 3;
        int width = getMeasuredWidth() / 3;

        int initHeight = 0;
        if (mGravity == Gravity.TOP) {
            initHeight = 0;
        } else if (mGravity == Gravity.CENTER) {
            initHeight = getMeasuredHeight() / 3 / 2;
        } else {
            initHeight = getMeasuredHeight() / 3;
        }

        //初始化圆圈区域，圆心点
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int circleX = width * j + width / 2;
                int circleY = initHeight + height * i + height / 2;
                //*3是为了放大范围（为了更好的体验）
                Rect rect = new Rect(circleX - 3 * mCircleRadius, circleY - 3 * mCircleRadius, circleX + 3 * mCircleRadius,                             circleY + 3 * mCircleRadius);
                mList.add(rect);

                CircleXY circleXY = new CircleXY(circleX, circleY);
                mCircleList.add(circleXY);
            }
        }

    
