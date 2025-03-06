# SwingSet3 Otomasyon Projesi

## Proje Açıklaması

Bu proje, **SwingSet3** adlı Swing tabanlı GUI uygulamasının otomatik test ve etkileşimini sağlamak için geliştirilmiş bir Java ajansı (agent) içerir. Bu ajans, Swing bileşenlerinin belirli özelliklerini manipüle etmeye ve bu bileşenler hakkında bilgi almaya olanak tanır. Ayrıca, bir HTTP sunucusu (Spark framework kullanılarak) aracılığıyla dışarıdan gelen isteklerle GUI bileşenlerine müdahale edebilir.

## Kullanım

Bu proje, **SwingSet3** uygulamasındaki bileşenleri keşfetmek, etkileşime girmek ve test senaryolarını çalıştırmak için kullanılan bir otomasyon aracıdır. Proje aşağıdaki adımlarla çalıştırılabilir.

### Gerekli Araçlar

- Java (JDK 8 veya üzeri)
- Spark Framework
- SwingSet3 Uygulaması

### Projeyi Çalıştırma

1. **Ajansı Dinamik Olarak Yükleyin**  
   Java ajansını uygulamaya dinamik olarak yükleyin. Bunun için `java -javaagent` komutunu kullanarak ajansı çalıştırabilirsiniz. Örnek komut:
   ```bash
   java -javaagent:/path/to/your/agent.jar -jar SwingSet3.jar
   ```

2. **HTTP Sunucusu**  
   Spark framework kullanılarak bir HTTP sunucusu başlatılır ve şu anda uygulama üzerinden çalışan Swing bileşenleri üzerinde işlem yapmanızı sağlar. Sunucu **8081** portunda çalışır.

### API Sonuçları

Ajans, aşağıdaki API uç noktalarını sağlar:

1. **GET /trigger**  
   Bu uç nokta, bir bileşenin etkileşime girilmesini sağlar (örneğin, bir butonun tıklanması veya bir metin alanının değiştirilmesi). Kullanımı:
   ```
   /trigger?path=/JButton[0]
   ```
   - `path`: Swing bileşeninin yolu (örneğin, `/JButton[0]`).

2. **GET /getText**  
   Bu uç nokta, belirli bir bileşenin metnini alır. Kullanımı:
   ```
   /getText?path=/JTextField[1]
   ```
   - `path`: Bileşenin yolu.

### Otomatik Bileşen Keşfi

Uygulama başlatıldığında, ajans mevcut tüm Swing bileşenlerini keşfeder ve her birini benzersiz bir **yol** ile kaydeder. Bu yollar, bileşenler üzerinde işlem yapmanızı sağlar.

Örneğin:
- `/JButton[0]`: İlk buton bileşeni.
- `/JTextField[1]`: İkinci metin alanı.

### Bileşen Etkileşimi

- **Butonlar**: `trigger` uç noktası ile butonları simüle edebilirsiniz.
- **Metin Alanları (TextFields)**: `getText` uç noktası ile metin alanlarının içeriğini alabilir ve `trigger` uç noktası ile metinlerini değiştirebilirsiniz.
- **Etiketler (Labels)**: Benzer şekilde etiketlerin metinlerini sorgulayabilirsiniz.

### Kodun İç Yapısı

Ajans, Swing bileşenlerinin keşfi ve etkileşimi için aşağıdaki metodları içerir:

- **`agentmain`**: Ajans başlatıldığında HTTP sunucusunu kurar ve Swing bileşenlerini keşfetmeye başlar.
- **`findAndStoreComponents`**: Swing bileşenlerini tarar ve her birini benzersiz bir yol ile kaydeder.
- **`triggerComponent`**: Belirli bir bileşeni tetikler (örneğin, bir butonun tıklanması).
- **`getText`**: Bir bileşenin metnini alır (örneğin, bir metin alanı veya etiket).

### Test Senaryoları

Bu otomasyon aracı, aşağıdaki test senaryoları için kullanılabilir:

1. **Buton Tıklama Testi**: `trigger` uç noktası ile belirli bir butonun tıklanması simüle edilebilir.
2. **Metin Alanı Değiştirme Testi**: `trigger` uç noktası ile metin alanlarının içeriği değiştirilip, `getText` ile doğrulama yapılabilir.
3. **Bileşen Doğrulama**: Bileşenlerin doğru şekilde keşfedilip keşfedilmediğini doğrulamak için `/getText` uç noktası ile bileşen metinlerinin doğruluğu kontrol edilebilir.
