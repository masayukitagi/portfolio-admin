package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet; //
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
public class SkillController {

  Map<String, String> map = new HashMap<>();

  public static final Logger logger = LoggerFactory.getLogger(SkillController.class); // ロガー出力(ログファイルに情報を残す。エラーが発生した原因を調査するためなど。)

  @RequestMapping(value = "/skillUpload", method = RequestMethod.GET)
  /*
   * @RequestMapping・・・ "http://localhost:8080/team20/skillUpload"
   * にアクセスするとこのコントローラが実行される。 【value属性】処理対象とするURLを指定。value属性が一つだけなら「value=」は省略可。
   * 【method属性】GETでアクセス元のリクエスト(method="get")を処理。
   * （@GetMapping("/skillUpload")が省略型っぽいけど使えなかった。）
   */
  public String skillUpload(Locale locale, Model model) throws IOException {
    logger.info("Welcome update! The client locale is {}.", locale);

    try {
      initialize();
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<Skill> skills = selectSkills();
    uploadSkill(skills);
    return "skillUpload";
  }

//-----------------------------ここまでタスク11。uploadSkill、initializeメソッドはタスク10--------------------------
//-----------------------------MySOLからデータの取得----------------------------

//jdbcTemplateを使えるようにしている。MySQLと接続の設定を補助してくれる。ライブラリ参照。
  @Autowired
  public JdbcTemplate jdbcTemplate; // jdbcTemplateを使えるようにしている。MySQLと接続の設定を補助してくれる。ライブラリ参照

  /*
   * (1)List型のメソッド「selectSkills」を定義 (2)String型の変数「sql」を定義。「skills」テーブルから全てのデータを取得。
   * (3)jdbcTemplateライブラリで
   * (4)rs(ResultSet)クラスのgetStringでcategoryを指定している。(Mapのkey,Mapのvalue)
   * (5)「Skill」の値を返却。rsリザルトセットのgetStringでnameを指定している。
   */

  public List<Skill> selectSkills() {
    final String sql = "select * from skills";
    return jdbcTemplate.query(sql, new RowMapper<Skill>() {
      public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Skill(rs.getString("category"), rs.getString("name"), rs.getInt("score"));
      }
    });
  }

//-----------------------------FirebaseSDKの初期化----------------------------
  private FirebaseApp app;

  // appを定義？
  public void initialize() throws IOException { // throwsは例外（IOException）を投げている
    // 以下４行はhttps://firebase.google.com/docs/admin/setup?hl=jaから引用。OAuth 2.0
    // 更新トークンを使用している。
    FileInputStream refreshToken = new FileInputStream(
        "/Users/tagimasayuki/seattle-key/dev-portfolio-b690c-firebase-adminsdk-1jnu4-25725fd231.json");
    FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(refreshToken))
        .setDatabaseUrl("https://dev-portfolio-b690c.firebaseio.com/").build();
    app = FirebaseApp.initializeApp(options, "other"); // TODO 不明
  }

//---------------------------------データの保存---------------------------------

//https://firebase.google.com/docs/database/admin参照。
  public void uploadSkill(List<Skill> skills) {
    final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
    DatabaseReference ref = database.getReference("skills");

    // ----------------------------データの取得----------------------------
    /*
     * (1)List型の変数「dataList」を定義。返す値はMap<String, Object>型。 (2)Map<String,
     * Object>方の変数「map」を定義。（未代入） (3)skills.stream();
     * (4)拡張for文。バックエンドのキーに3つのバリュー。を3回回している。 (5)mapの初期化(型を指定していない) (6)map.put(key,
     * value); putメソッドはキーと値をHashMapに返却。左はString型なので文字列。
     * (7)「entry.getValue」でvalueを返却。
     */

    List<Map<String, Object>> dataList = new ArrayList<>();
    Map<String, Object> map;
    skills.stream();
    Map<String, List<Skill>> skillMap = skills.stream().collect(Collectors.groupingBy(Skill::getCategory));
    for (Map.Entry<String, List<Skill>> entry : skillMap.entrySet()) {
      map = new HashMap<String, Object>();
      map.put("category", entry.getKey());
      map.put("skills", entry.getValue());

      dataList.add(map);
    }

// ---------------------リアルタイムデータベース更新---------------------
    /*
     * (1)上でテーブルの値を取得したList型の変数「dataList」 (2) (3) (4) (5) (6)
     */
    ref.setValue(dataList, new DatabaseReference.CompletionListener() {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
          System.out.println("Data could be saved" + databaseError.getMessage());
        } else {
          System.out.println("Data save successfully.");
        }
      }
    });

    // スキルマップの状態が上の定義。バックエンドのキーに3つのバリュー。を3回回している。拡張for文。datakist.add.mapに入れればいいから・・？for文の中で毎回データの初期化を行う必要があるのでその中に
    // map = new HashMap();を入れる。map.put

    /*
     * /save-data?hl=ja)--------------------- public void uploadSkill() { final
     * FirebaseDatabase database = FirebaseDatabase.getInstance(app); //app引数
     * DatabaseReference ref = database.getReference("skills"); //引数app
     *
     * //【エラー解決】「nullPointerException」
     * 「}」がここにあった。アノテーションクラス（servis,controller,erpositoryとか）
     * がDIコンテナに詰め込まれれてることを依存性の注入という。jdbcTemplateライブラリによりSQLと繋がるが、
     * それより先にインスタンス変数listが定義されて動いてしまったため、そこでjdbcTemplateを認識できなかった。
     *
     *
     * //---------------------データの取得（MySQL-Java)と整形（MySQL-Java →
     * Java-Firebase）---------------------
     *
     * List<Map<String, Object>> list =
     * jdbcTemplate.query("select category, name, score from skills", //Mapインタフェース :
     * キーに対してキーに紐づく値を保持することができる。後からサイズを変更できる動的配列的な。
     *
     *
     * @SuppressWarnings({ "rawtypes", "unchecked" })
     * //非推薦のメソッドであるなどの理由でコンパイル時に警告を出すものである場合、コンパイルする度に警告が出てしまうため、@
     * SuppressWarningsを先頭に置くことで警告を抑制してくれる効果がある。 public Map<String, Object>
     * mapRow(ResultSet rs, int rowNum) throws SQLException {
     * //１行１行に対してResultSetが１レコード（行）を表している Map<String, Object> map = new HashMap();
     * //Mapを初期化（再定義）。インターフェースHashMapに沿ってMap<String, Object>型の変数mapを定義。
     *
     * //Mapに返される１レコードが、nameとcapital_cityをプリフェクチャーズテーブルから取得し、Mapに詰めている。これを１行１行やっている。
     * //上記はとりあえず呪文だと思えば良い。 //maprowというメソッドを持ったrowmaperを渡している。470行目それがqueryのやつ・・？
     *
     * return map; }
     *
     * });
     */

  }
}
