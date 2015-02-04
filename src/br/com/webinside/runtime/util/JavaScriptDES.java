/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.util;

public class JavaScriptDES {

  private String[] keys;
  private boolean mini=false;

  public JavaScriptDES(String key) {
    setKey(key);
  }

  public JavaScriptDES(String key, boolean mini) {
    this.mini=mini;
    setKey(key);
  }

  public void setMini(boolean mini) {
    this.mini=mini;
  }

  // Utiliza os 8 primeiros caracteres
  public void setKey(String key) {
    if (key==null) key="";
    String hexkey="";
    for (int i=0;i<key.length();i++) {
      int num = (int)key.charAt(i);
      hexkey=hexkey+Integer.toHexString(num);
    }
    if (hexkey.length()>16) hexkey = hexkey.substring(0,16);
    else while (hexkey.length()<16) hexkey=hexkey+"00";
    keys = keyGeneration(hexkey);
  }

  public String encode(String text) {
    if (text==null) text="";
    StringBuffer resp = new StringBuffer();
    while (text.length()>0) {
      String seg = StringA.mid(text, 0, 7);
      while (seg.length() < 8) seg += ' ';
      text = StringA.mid(text,8,text.length()-1);
      String hex = "";
      for (int i=0;i<seg.length();i++) {
        int num = (int)seg.charAt(i);
        String hexnum = Integer.toHexString(num);
        if (hexnum.length()<2) hexnum="0"+hexnum;
        hex = hex+hexnum;
      }
      resp.append(encodeProtected(hex));
    }
    return resp.toString();
  }

  public String decode(String text) {
    if (text==null) text="";
    String range="0123456789abcdef";
    String hex="";
    while (text.length()>0) {
      String seg = StringA.mid(text, 0, 15).toLowerCase();
      while (seg.length() < 16) seg += '0';
      text = StringA.mid(text,16,text.length());
      String tested="";
      for (int i=0;i<seg.length();i++) {
        if (range.indexOf(seg.charAt(i))>-1) tested=tested+seg.charAt(i);
        else tested=tested+"0";
      }
      hex = hex+decodeProtected(tested);
    }
    StringBuffer resp = new StringBuffer();
    for (int i=0;i<hex.length();i=i+2) {
      String hexnum = hex.charAt(i)+""+hex.charAt(i+1);
      int num = Integer.parseInt(hexnum,16);
      resp.append((char)num);
    }
    return resp.toString();
  }

  // Deve ter 16 digitos
  protected String encodeProtected(String hex16) {
    String bin = hexToBinary(hex16);
    String IP = "";
    if (!mini) IP = dataPermute(bin,1,true);
    else IP = bin;
    String[] Lvector = new String[17];
    Lvector[0] = StringA.mid(IP,0,31);
    String[] Rvector = new String[17];
    Rvector[0] = StringA.mid(IP,32,63);
    int iterations = 16;
    if (mini) iterations=4;
    for (int i=1;i<=iterations;i++) {
      Lvector[i] = Rvector[i-1];
      String func = dataFunction(Rvector,i,true);
      String add="";
      for (int a=0;a<32;a++) {
        char n1 = Lvector[i-1].charAt(a);
        char n2 = func.charAt(a);
        if (n1==n2) add=add+"0";
        else add=add+"1";
      }
      Rvector[i] = add;
    }
    String encbin = "";
    if (!mini) encbin = dataPermute(Rvector[iterations]+Lvector[iterations],3,true);
    else encbin = Rvector[iterations]+Lvector[iterations];
    return binToHex(encbin);
  }

  // Deve ter 16 digitos
  protected String decodeProtected(String hex16) {
    String binario = hexToBinary(hex16);
    String encbin = "";
    if (!mini) encbin = dataPermute(binario,3,false);
    else encbin = binario;
    int iterations=16;
    if (mini) iterations=4;
    String[] Lvector = new String[iterations+1];
    Lvector[iterations] = StringA.mid(encbin,32,63);
    String[] Rvector = new String[iterations+1];
    Rvector[iterations] = StringA.mid(encbin,0,31);
    for (int i=iterations-1;i>=0;i--) {
      Rvector[i]=Lvector[i+1];
      String func = dataFunction(Rvector,i,false);
      String add="";
      for (int a=0;a<32;a++) {
        char n1 = Rvector[i+1].charAt(a);
        char n2 = func.charAt(a);
        if (n1==n2) add=add+"0";
        else add=add+"1";
      }
      Lvector[i] = add;
    }
    String join = Lvector[0]+Rvector[0];
    String source = "";
    if (!mini) source = dataPermute(join,1,false);
    else source = join;
    return binToHex(source);
  }

  private String hexToBinary(String hex) {
    StringBuffer resp = new StringBuffer();
    for (int i=0;i<hex.length();i++) {
      char let = hex.charAt(i);
      int num = Integer.parseInt(let+"",16);
      String letbin = Integer.toBinaryString(num);
      for (int a=1;a<=4-letbin.length();a++) resp.append("0");
      resp.append(letbin);
    }
    return resp.toString();
  }

  private String binToHex(String bin) {
    StringBuffer resp = new StringBuffer();
    for (int i=1;i<=16;i++) {
      String seg = StringA.mid(bin,(i*4)-4,(i*4)-1);
      int code = Integer.parseInt(seg,2);
      resp.append(Integer.toHexString(code));
    }
    return resp.toString();
  }

  private String[] keyGeneration(String hexkey) {
    if (hexkey==null) hexkey="";
    String keybinary = hexToBinary(hexkey);
    String keypermute = "";
    if (!mini) keypermute = keyPermute(keybinary,1);
    else keypermute = keybinary;
    return keyCreateSubKeys(keypermute);
  }

  private String[] keyCreateSubKeys(String permute) {
     String C = StringA.mid(permute,0,27);
     String D = StringA.mid(permute,28,55);
     String[] Cvector = new String[17];
     Cvector[0] = C;
     String[] Dvector = new String[17];
     Dvector[0] = D;
     for (int i=1;i<=16;i++) {
        int qnt=2;
        if ((i==1)||(i==2)||(i==9)||(i==16)) qnt=1;
        Cvector[i] = keyLeftShift(Cvector[i-1],qnt);
        Dvector[i] = keyLeftShift(Dvector[i-1],qnt);
     }
     String[] Kvector = new String[16];
     for (int i=0;i<16;i++) {
       String cn = Cvector[i+1];
       String dn = Dvector[i+1];
       if (!mini) Kvector[i] = keyPermute(cn+dn,2);
       else Kvector[i] = cn+dn;
     }
     return Kvector;
  }

  private String keyLeftShift(String binary, int qnt) {
    String p1 = StringA.mid(binary,0,qnt-1);
    String p2 = StringA.mid(binary,qnt,binary.length()-1);
    return p2+p1;
  }

  private String dataFunction(String[] Rvector, int n, boolean encode) {
    if (!encode) n=n+1;
    String E = dataExpandKey(Rvector[n-1]);
    String K = keys[n-1];
    String resp = "";
    for (int i=0;i<48;i++) {
      char c1 = E.charAt(i);
      char c2 = K.charAt(i);
      if (c1==c2) resp=resp+"0";
      else resp=resp+"1";
    }
    String resp2 = "";
    for (int i=1;i<=8;i++) {
      String seg = StringA.mid(resp,(6*i)-6,(6*i)-1);
      resp2=resp2+receiveS(seg,i);
    }
    if (mini) return resp2;
    return dataPermute(resp2,2,true);
  }

  private String keyPermute(String keybin, int num) {
    String matrix="";
    if (num==1) {
          matrix = "57,49,41,33,25,17,9,"+"1,58,50,42,34,26,18,"+
          "10,2,59,51,43,35,27,"+"19,11,3,60,52,44,36,"+"63,55,47,39,31,23,15,"+
          "7,62,54,46,38,30,22,"+"14,6,61,53,45,37,29,"+"21,13,5,28,20,12,4";
    } else if (num==2) {
          matrix = "14,17,11,24,1,5,"+"3,28,15,6,21,10,"+
          "23,19,12,4,26,8,"+"16,7,27,20,13,2,"+"41,52,31,37,47,55,"+
          "30,40,51,45,33,48,"+"44,49,39,56,34,53,"+"46,42,50,36,29,32";
    }
    String resp="";
    int count = StringA.count(matrix,',')+1;
    for (int i=1;i<=count;i++) {
      int pos = Integer.parseInt(StringA.piece(matrix,",",i));
      resp=resp+keybin.charAt(pos-1);
    }
    return resp;
  }

  private String dataPermute(String keybin, int num, boolean encode) {
    String matrix="";
    if (num==1) {
      // IP
      matrix = "58,50,42,34,26,18,10,2,"+"60,52,44,36,28,20,12,4,"+
      "62,54,46,38,30,22,14,6,"+"64,56,48,40,32,24,16,8,"+"57,49,41,33,25,17,9,1,"+
      "59,51,43,35,27,19,11,3,"+"61,53,45,37,29,21,13,5,"+"63,55,47,39,31,23,15,7";
    } else if (num==2) {
      // P
      matrix = "16,7,20,21,"+"29,12,28,17,"+"1,15,23,26,"+
        "5,18,31,10,"+"2,8,24,14,"+"32,27,3,9,"+"19,13,30,6,"+"22,11,4,25";
    } else if (num==3) {
      // IP-1
      matrix = "40,8,48,16,56,24,64,32,"+"39,7,47,15,55,23,63,31,"+
        "38,6,46,14,54,22,62,30,"+"37,5,45,13,53,21,61,29,"+"36,4,44,12,52,20,60,28,"+
        "35,3,43,11,51,19,59,27,"+"34,2,42,10,50,18,58,26,"+"33,1,41,9,49,17,57,25";
    }
    String resp="";
    if (encode) {
      int count = StringA.count(matrix,',')+1;
      for (int i=1;i<=count;i++) {
        int pos = Integer.parseInt(StringA.piece(matrix,",",i));
        resp=resp+keybin.charAt(pos-1);
      }
    } else {
      char[] vetor = new char[64];
      if (num==2) vetor = new char[32];
      int count = StringA.count(matrix,',')+1;
      for (int i=1;i<=count;i++) {
        int pos = Integer.parseInt(StringA.piece(matrix,",",i));
        vetor[pos-1]=keybin.charAt(i-1);
      }
      for (int i=0;i<vetor.length;i++) resp=resp+vetor[i];
    }
    return resp;
  }

  private String dataExpandKey(String keyvector) {
    String matrix = "32,1-5,4,5-9,8,9-13,12,13-17,16,17-21,20,21-25,24,25-29,28,29-32,1";
    String resp="";
    int count = StringA.count(matrix,',')+1;
    for (int i=1;i<=count;i++) {
      String code = StringA.piece(matrix,",",i);
      if (code.indexOf("-")==-1) {
        int pos = Integer.parseInt(code);
        resp=resp+keyvector.charAt(pos-1);
      } else {
        int pos1 = Integer.parseInt(StringA.piece(code,"-",1));
        int pos2 = Integer.parseInt(StringA.piece(code,"-",2));
        resp=resp+StringA.mid(keyvector,pos1-1,pos2-1);
      }
    }
    return resp;
  }

  private String receiveS(String digits, int s) {
    String[] matrix = matrixS(s);
    String istr = digits.charAt(0)+""+digits.charAt(5);
    int i = Integer.parseInt(istr,2);
    String jstr = digits.substring(1,5);
    int j = Integer.parseInt(jstr,2);
    String code = StringA.piece(matrix[i],",",j+1);
    int icode = Integer.parseInt(code);
    String hex = Integer.toHexString(icode);
    return hexToBinary(hex.charAt(0)+"");
  }

  private String[] matrixS(int num) {
    String[] matrix = new String[4];
    if (num==1) {
      matrix[0]="14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7";
      matrix[1]="0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8";
      matrix[2]="4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0";
      matrix[3]="15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13";
      return matrix;
    } else if (num==2)  {
      matrix[0]="15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10";
      matrix[1]="3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5";
      matrix[2]="0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15";
      matrix[3]="13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9";
      return matrix;
    } else if (num==3) {
      matrix[0]="10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8";
      matrix[1]="13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1";
      matrix[2]="13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7";
      matrix[3]="1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12";
      return matrix;
    } else if (num==4) {
      matrix[0]="7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15";
      matrix[1]="13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9";
      matrix[2]="10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4";
      matrix[3]="3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14";
      return  matrix;
    } else if (num==5) {
      matrix[0]="2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9";
      matrix[1]="14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6";
      matrix[2]="4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14";
      matrix[3]="11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3";
      return matrix;
    } else if (num==6) {
      matrix[0]="12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11";
      matrix[1]="10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8";
      matrix[2]="9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6";
      matrix[3]="4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13";
      return matrix;
    } else if (num==7) {
      matrix[0]="4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1";
      matrix[1]="13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6";
      matrix[2]="1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2";
      matrix[3]="6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12";
      return matrix;
    } else if (num==8) {
      matrix[0]="13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7";
      matrix[1]="1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2";
      matrix[2]="7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8";
      matrix[3]="2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11";
      return matrix;
    }
    return null;
  }

}
