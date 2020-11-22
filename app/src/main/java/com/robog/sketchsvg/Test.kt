package com.robog.sketchsvg

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * Created by yuxingdong on 2020/11/21.
 *
 *
 * 1、当hasCurveFrom为true时，则curveFrom的点为贝塞尔曲线起点，
 * 控制点为下一个点的curveTo，终点为下一个点的point。
 * 2、当hasCurveFrom为false时，L到该点（线段）
 * 3、point中的点坐标转为svg的坐标公式：
 * xsvg = frame.x + xlayer * frame.width
 * ysvg = frame.y + ylayer * frame.height
 *
 * 补充说明：该样例只展示sketch如何转成svg中的点，
 * 至于路径是否需要闭合（拼接"z"）、以及Frame中的宽高和xy坐标，需解析json中的相应字段。
 */
object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val jsonArray = JSONArray.parse(WISH) as JSONArray
        val sb = StringBuilder()
        var firstJson: JSONObject? = null
        jsonArray.forEachIndexed { index, _ ->
            val json = jsonArray[index] as JSONObject
            val curveMode = getCurveMode(json)
            val hasCurveFrom = hasCurveFrom(json)
            val hasCurveTo = hasCurveTo(json)
            val curveFrom = curveFrom(json)
            val curveTo = curveTo(json)
            val point = point(json)

            // 起始位置，第一个点也可能包含最后一个点控制点信息
            if (index == 0) {
                firstJson = json
                sb.append("M")
                    .appendPoint(point)
            }

            // 第一个点以后，优先拼接上一个点控制点信息
            if (index > 0 && hasCurveTo) {
                val lastArray = jsonArray[index - 1] as JSONObject
                val lastHasFrom = hasCurveFrom(lastArray)
                if (lastHasFrom) {
                    sb.appendPoint(curveTo)
                        .appendPoint(point)
                }
            }
            if (!hasCurveFrom) {
                // 线段
                sb.append("L")
                    .appendPoint(point)
            } else {
                sb.append("C")
                    .appendPoint(curveFrom)
                // 最后一个点控制点信息（第一个点中）
                if (index == jsonArray.size - 1 && firstJson != null) {
                    val firstCurveTo = curveTo(firstJson!!)
                    val firstPoint = point(firstJson!!)
                    sb.appendPoint(firstCurveTo)
                        .appendPoint(firstPoint)
                }
            }

            println("""
                mode: $curveMode, from: $hasCurveFrom, to: $hasCurveTo
                from: (${curveFrom.toX()}, ${curveFrom.toY()})
                to: (${curveTo.toX()}, ${curveTo.toY()})
                point: (${point.toX()}, ${point.toY()})
                -------------------------------
            """.trimIndent())
        }

        println(sb.toString())
    }

    private fun point(json: JSONObject) = json["point"]!!.toPair()

    private fun curveTo(json: JSONObject) = json["curveTo"]!!.toPair()

    private fun curveFrom(json: JSONObject) = json["curveFrom"]!!.toPair()

    private fun hasCurveTo(json: JSONObject) = json["hasCurveTo"] as Boolean

    private fun hasCurveFrom(json: JSONObject) = json["hasCurveFrom"] as Boolean

    private fun getCurveMode(json: JSONObject) = json["curveMode"] as Int

    private fun Any.toPair(): Pair<Double, Double> {
        val data = toString().substring(1, toString().length - 1)
        val split = data.split(",".toRegex())
        return Pair(split[0].toDouble(), split[1].toDouble())
    }

    private fun StringBuilder.appendPoint(point: Pair<Double, Double>): StringBuilder {
        append(point.toX().toString())
        append(",")
        append(point.toY().toString())
        append(" ")
        return this
    }

    private fun Pair<Double, Double>.toX(): Double {
        return first * FRAME_WIDTH + FRAME_X
    }

    private fun Pair<Double, Double>.toY(): Double {
        return second * FRAME_HEIGHT + FRAME_Y
    }


    private const val FRAME_WIDTH = 14.625
    private const val FRAME_HEIGHT = 13.5
    private const val FRAME_X = 5.049038461538462
    private const val FRAME_Y = 5.5

    private const val WISH = "[\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.97275668269230786, 0.14409752604166662}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.87019206730769227, 0.027777499999999913}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.91826923076923073, 0.083333333333333329}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{1, 0.3936636328125}\",\n" +
            "        \"curveMode\":2,\n" +
            "        \"curveTo\":\"{1, 0.21831553385416669}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{1, 0.30598958333333331}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.91826923076923073, 0.52864583333333337}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.97275668269230786, 0.46788164062500004}\",\n" +
            "        \"hasCurveFrom\":false,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.91826923076923073, 0.52864583333333337}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.5, 1}\",\n" +
            "        \"curveMode\":1,\n" +
            "        \"curveTo\":\"{0.5, 1}\",\n" +
            "        \"hasCurveFrom\":false,\n" +
            "        \"hasCurveTo\":false,\n" +
            "        \"point\":\"{0.5, 1}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.027243317307692326, 0.46788164062500004}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.081730769230769232, 0.52864583333333337}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":false,\n" +
            "        \"point\":\"{0.081730769230769232, 0.52864583333333337}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0, 0.21831553385416669}\",\n" +
            "        \"curveMode\":2,\n" +
            "        \"curveTo\":\"{0, 0.3936636328125}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0, 0.30598958333333331}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.1298079326923077, 0.027777499999999913}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.027243317307692326, 0.14409752604166662}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.081730769230769232, 0.083333333333333329}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.32812534855769226, 0}\",\n" +
            "        \"curveMode\":2,\n" +
            "        \"curveTo\":\"{0.18870157451923078, 0}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.25841346153846156, 0}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.43509615384615385, 0.083333333333333329}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.3870189903846154, 0.027777499999999913}\",\n" +
            "        \"hasCurveFrom\":false,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.43509615384615385, 0.083333333333333329}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.5, 0.15625}\",\n" +
            "        \"curveMode\":1,\n" +
            "        \"curveTo\":\"{0.5, 0.15625}\",\n" +
            "        \"hasCurveFrom\":false,\n" +
            "        \"hasCurveTo\":false,\n" +
            "        \"point\":\"{0.5, 0.15625}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.61538461538461542, 0.027777499999999913}\",\n" +
            "        \"curveMode\":4,\n" +
            "        \"curveTo\":\"{0.56730769230769229, 0.083333333333333329}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":false,\n" +
            "        \"point\":\"{0.56730769230769229, 0.083333333333333329}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"_class\":\"curvePoint\",\n" +
            "        \"cornerRadius\":0,\n" +
            "        \"curveFrom\":\"{0.81169906250000001, 0}\",\n" +
            "        \"curveMode\":2,\n" +
            "        \"curveTo\":\"{0.673877860576923, 0}\",\n" +
            "        \"hasCurveFrom\":true,\n" +
            "        \"hasCurveTo\":true,\n" +
            "        \"point\":\"{0.74278846153846156, 0}\"\n" +
            "    }\n" +
            "]"

    private const val HOME = "[\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.39903846153846151, 1}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.39903846153846151, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.39903846153846151, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.15024038461538458, 1}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.15024038461538458, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.15024038461538458, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.15024038461538458, 0.52891396332863183}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.15024038461538458, 0.52891396332863183}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.15024038461538458, 0.52891396332863183}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0, 0.52891396332863183}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0, 0.52891396332863183}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0, 0.52891396332863183}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.5, 0}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.5, 0}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.5, 0}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{1, 0.52891396332863183}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{1, 0.52891396332863183}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{1, 0.52891396332863183}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.8497596153846152, 0.52891396332863183}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.8497596153846152, 0.52891396332863183}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.8497596153846152, 0.52891396332863183}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.8497596153846152, 1}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.8497596153846152, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.8497596153846152, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.60096153846153832, 1}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.60096153846153832, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.60096153846153832, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.60096153846153832, 0.64739069111424541}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.60096153846153832, 0.64739069111424541}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.60096153846153832, 0.64739069111424541}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.39903846153846151, 0.64739069111424541}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.39903846153846151, 0.64739069111424541}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.39903846153846151, 0.64739069111424541}\"\n" +
            "                }\n" +
            "              ]"

    private const val CART = "[\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.76471599444865934, 0.73333333333333328}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.72722991628941136, 0.73333333333333328}\",\n" +
            "                  \"hasCurveFrom\": true,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.72722991628941136, 0.73333333333333328}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.81469743199432343, 0.66466666666666674}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.79770374322879767, 0.70599999999999996}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": true,\n" +
            "                  \"point\": \"{0.81469743199432343, 0.66466666666666674}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{1.0121241102996963, 0.18800000000000003}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.99363097840780057, 0.23200000000000004}\",\n" +
            "                  \"hasCurveFrom\": true,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.99363097840780057, 0.23200000000000004}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.95014712774307286, 0.13333333333333333}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.98813302027777761, 0.13333333333333333}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": true,\n" +
            "                  \"point\": \"{0.95014712774307286, 0.13333333333333333}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.21042185206724548, 0.13333333333333333}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.21042185206724548, 0.13333333333333333}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.21042185206724548, 0.13333333333333333}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.16343930077432128, 0}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.16343930077432128, 0}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.16343930077432128, 0}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0, 0}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0, 0}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0, 0}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0, 0.13333333333333333}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0, 0.13333333333333333}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0, 0.13333333333333333}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.099962875091328021, 0.13333333333333333}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.099962875091328021, 0.13333333333333333}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.099962875091328021, 0.13333333333333333}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.27989605025571845, 0.63933333333333331}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.27989605025571845, 0.63933333333333331}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.27989605025571845, 0.63933333333333331}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.17593466016073728, 0.89133333333333331}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.21242110956907204, 0.80199999999999994}\",\n" +
            "                  \"hasCurveFrom\": true,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.21242110956907204, 0.80199999999999994}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.29988862527398408, 1}\",\n" +
            "                  \"curveMode\": 4,\n" +
            "                  \"curveTo\": \"{0.22391684020457478, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": true,\n" +
            "                  \"point\": \"{0.29988862527398408, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.89966587582195212, 1}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.89966587582195212, 1}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.89966587582195212, 1}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.89966587582195212, 0.8666666666666667}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.89966587582195212, 0.8666666666666667}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.89966587582195212, 0.8666666666666667}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.29988862527398408, 0.8666666666666667}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.29988862527398408, 0.8666666666666667}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.29988862527398408, 0.8666666666666667}\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"_class\": \"curvePoint\",\n" +
            "                  \"cornerRadius\": 0,\n" +
            "                  \"curveFrom\": \"{0.35486820657421442, 0.73333333333333328}\",\n" +
            "                  \"curveMode\": 1,\n" +
            "                  \"curveTo\": \"{0.35486820657421442, 0.73333333333333328}\",\n" +
            "                  \"hasCurveFrom\": false,\n" +
            "                  \"hasCurveTo\": false,\n" +
            "                  \"point\": \"{0.35486820657421442, 0.73333333333333328}\"\n" +
            "                }\n" +
            "              ]"
}