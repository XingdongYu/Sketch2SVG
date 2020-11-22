## SketchSVG

Sketch中的icon数据（json中的points）转成svg遵循以下规则：

1. 当hasCurveFrom为true时，则curveFrom的点为贝塞尔曲线起点，控制点为下一个点的curveTo，终点为下一个点的point。
2. 当hasCurveFrom为false时，即L到该点（线段）
3. point中的点坐标转为svg的坐标公式：
   xsvg = frame.x + xlayer * frame.width
   ysvg = frame.y + ylayer * frame.height

补充说明：该样例只展示sketch如何转成svg中的点，至于路径是否需要闭合（拼接"z"）、以及Frame中的宽高和xy坐标，需解析json中的相应字段。
