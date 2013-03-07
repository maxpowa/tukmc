/*    */ package javaQuery.j2ee;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ 
/*    */ public class tinyURL
/*    */ {
/*    */   private String _result;
/*    */   public String Terms_Condition;
/*    */ 
/*    */   public tinyURL()
/*    */   {
/* 20 */     this.Terms_Condition = "Thank You www.tinyurl.com.";
/*    */   }
/*    */ 
/*    */   public String getTinyURL(String LongURL)
/*    */   {
/* 25 */     if (LongURL.startsWith("http://"))
/* 26 */       LongURL = LongURL.replace("http://", "");
/* 27 */     if (LongURL.startsWith("https://"))
/* 28 */       LongURL = LongURL.replace("https://", "");
/* 29 */     if (LongURL.startsWith("ftp://"))
/* 30 */       LongURL = LongURL.replace("ftp://", "");
/*    */     try
/*    */     {
/* 33 */       URL DataURL = new URL("http://tinyurl.com/create.php?url=" + LongURL);
/* 34 */       URLConnection openURL = DataURL.openConnection();
/* 35 */       openURL.addRequestProperty("User-Agent", "Mozilla/4.76");
/* 36 */       BufferedReader in = new BufferedReader(new InputStreamReader(openURL.getInputStream()));
/*    */       String inputLine;
/* 40 */       while ((inputLine = in.readLine()) != null)
/*    */       {
/* 42 */         if (inputLine.contains("<small>["))
/*    */         {
/* 44 */           int smallStart = inputLine.indexOf("<small>");
/* 45 */           int smallEnd = inputLine.indexOf("</small>");
/* 46 */           this._result = inputLine.substring(smallStart, smallEnd + 8);
/* 47 */           int hrefStart = this._result.indexOf("href=\"");
/* 48 */           int hrefEnd = this._result.indexOf("\"", hrefStart + 6);
/* 49 */           this._result = this._result.substring(hrefStart + 6, hrefEnd);
/*    */         }
/*    */       }
/* 52 */       in.close();
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/* 56 */       this._result = "Check internet connection";
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 60 */       this._result = e.getMessage();
/*    */     }
/* 62 */     return this._result;
/*    */   }
/*    */ }

/* Location:           C:\Users\V\Downloads\javaQuery 7.0.jar
 * Qualified Name:     javaQuery.j2ee.tinyURL
 * JD-Core Version:    0.6.2
 */