package com.seattleacademy.team20;

//データの整形

public class Skill {
  // フィールド（クラス直下の属性郡）
  private String category;
  private String name;
  private int score;

  // コンストラクタ（インスタンス化と同時にクラスに値を投げる。同じインスタンスを生成しないため。）
  public Skill(String category, String name, int score) { // データ型 値

    this.category = category;
    this.name = name;
    this.score = score;
    // this.でインスタンスに値を渡す。

  }

  public String getCategory() {// getter・・private外部へ値を渡す。（Firebasへ)
    return category;
  }

  public void setCategory(String category) {// setter・・private内部へ値を取得。(MySQLから)
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

}
