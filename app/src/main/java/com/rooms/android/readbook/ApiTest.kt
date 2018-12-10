package com.rooms.android.readbook

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.io.*
import java.util.*


class ApiTest {

    companion object {

        val TAG = ApiTest::class.simpleName
        val STORAGE = Environment.getExternalStorageDirectory().absolutePath

        val audioPlay = MediaPlayer()

        fun TTS(srcText: String) {
            Log.d(TAG, "fun TTS srcText : " + srcText)

            Thread({
                kotlin.run {
                    try {
                        var text = URLEncoder.encode(srcText, "UTF-8")
                        val apiUrl = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts"
                        var url = URL(apiUrl)
                        var con = url.openConnection() as HttpURLConnection
                        con.requestMethod = "POST"
                        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", AppInfo.CLIENT_ID);
                        con.setRequestProperty("X-NCP-APIGW-API-KEY", AppInfo.CLIENT_SECRET);

                        val postParams = "speaker=mijin&speed=10&text=$text"
                        con.doOutput = true
                        val wr = DataOutputStream(con.outputStream)
                        wr.writeBytes(postParams)
                        wr.flush()
                        wr.close()

                        val responseCode = con.responseCode
                        Log.d(TAG, "fun TTS responseCode : " + responseCode)

                        val br: BufferedReader
                        if (responseCode == 200) { // 정상 호출
                            val inputStream = con.inputStream
                            var read = 0
                            val bytes = ByteArray(1024)
                            // 랜덤한 이름으로 mp3 파일 생성
                            val tempname = STORAGE + "/" + java.lang.Long.valueOf(Date().getTime()).toString()
                            val f = File(tempname + ".mp3")
                            f.createNewFile()
                            val outputStream = FileOutputStream(f)
                            do {
                                read = inputStream.read(bytes)

                                if (read == -1) {
                                    break;
                                }
                                outputStream.write(bytes, 0, read)

                            } while (true)
                            inputStream.close()

                            audioPlay.reset()
                            audioPlay.setDataSource(tempname)
                            audioPlay.prepare()
                            audioPlay.start()

                        } else {  // 에러 발생
                            br = BufferedReader(InputStreamReader(con.errorStream))
                            var inputLine: String
                            val response = StringBuffer()
                            do {
                                inputLine = br.readLine()
                                if (inputLine == null) {
                                    break;
                                }
                                response.append(inputLine)

                            } while (true)
                            br.close()
                            println(response.toString())
                        }
                    } catch (e: Exception) {
                        println(e.toString())
                    }
                }

            }).start()

        }
    }

}