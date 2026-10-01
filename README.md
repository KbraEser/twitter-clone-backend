# Twitter Clone

Spring Boot ile geliştirilmiş, JWT tabanlı kimlik doğrulamaya sahip basit bir Twitter benzeri REST API.
Kullanıcılar kayıt olabilir, giriş yapabilir, tweet atabilir (retweet dahil), yorum yapabilir ve tweet/yorumları beğenebilir.

## Teknolojiler

- Java 17
- Spring Boot 4.0.6 (Web MVC, Data JPA, Security, Validation)
- PostgreSQL
- JWT (jjwt 0.9.1)
- Lombok
- Maven
- Spring REST Docs (test)

## Gereksinimler

- JDK 17+
- PostgreSQL (varsayılan: `localhost:5432`)

## Kurulum ve Çalıştırma

1. Veritabanını oluşturun:

   ```sql
   CREATE DATABASE twitter;
   ```

2. `src/main/resources/application.properties` dosyasındaki veritabanı bilgilerini kendi ortamınıza göre düzenleyin:

   ```properties
   server.port=3000
   spring.datasource.url=jdbc:postgresql://localhost:5432/twitter
   spring.datasource.username=<kullanici_adi>
   spring.datasource.password=<sifre>
   spring.jpa.hibernate.ddl-auto=update
   ```

   Tablolar (`users`, `tweets`, `comments`, `likes`) `ddl-auto=update` ile otomatik oluşturulur.

3. Uygulamayı başlatın:

   ```bash
   ./mvnw spring-boot:run
   ```

   API `http://localhost:3000` adresinde çalışır.

4. Testleri çalıştırmak için:

   ```bash
   ./mvnw test
   ```

## Kimlik Doğrulama

Giriş yapıldığında dönen JWT, korumalı isteklerde `Authorization` başlığıyla gönderilmelidir:

```
Authorization: Bearer <token>
```

Token geçerlilik süresi 10 saattir. Oturum yönetimi stateless'tır.

## API Uç Noktaları

| Metot  | Yol                       | Açıklama                                  | Yetki |
|--------|---------------------------|-------------------------------------------|-------|
| POST   | `/auth/register`          | Yeni kullanıcı kaydı                      | Açık  |
| POST   | `/auth/login`             | Giriş yap, JWT al                         | Açık  |
| GET    | `/tweet/findAll`          | Tüm tweetleri listele                     | Açık  |
| GET    | `/tweet/findById?id=`     | ID ile tweet getir                        | Açık  |
| GET    | `/tweet/findByUserId?id=` | Bir kullanıcının tweetlerini getir        | Açık  |
| POST   | `/tweet`                  | Tweet at / retweet yap                    | USER  |
| PUT    | `/tweet/{id}`             | Kendi tweetini güncelle                   | USER  |
| DELETE | `/tweet/{id}`             | Kendi tweetini sil                        | USER  |
| GET    | `/comment/findByTweetId?id=` | Bir tweetin yorumlarını listele        | Açık  |
| POST   | `/comment`                | Yorum yap                                 | USER  |
| PUT    | `/comment/{id}`           | Kendi yorumunu güncelle                   | USER  |
| DELETE | `/comment/{id}`           | Kendi yorumunu sil                        | USER  |
| POST   | `/like`                   | Tweet veya yorumu beğen                   | USER  |
| POST   | `/dislike`                | Beğeniyi geri al                          | USER  |

### Örnek İstekler

**Kayıt**

```http
POST /auth/register
Content-Type: application/json

{
  "name": "Ayşe",
  "surname": "Yılmaz",
  "email": "ayse@example.com",
  "password": "123456",
  "picture": null
}
```

Şifre 6–20 karakter olmalıdır.

**Giriş**

```http
POST /auth/login
Content-Type: application/json

{
  "email": "ayse@example.com",
  "password": "123456"
}
```

Yanıt: `token`, kullanıcı `id`, `email`, `name` ve `surname` bilgilerini içerir.

**Tweet atma**

```http
POST /tweet
Authorization: Bearer <token>
Content-Type: application/json

{ "content": "Merhaba dünya!" }
```

Retweet için `content` boş bırakılıp `tweetId` verilir:

```json
{ "tweetId": 1 }
```

`content` ve `tweetId` alanlarından en az biri dolu olmalıdır.

**Yorum yapma**

```http
POST /comment
Authorization: Bearer <token>
Content-Type: application/json

{ "content": "Güzel tweet!", "tweetId": 1 }
```

Yorum en fazla 200 karakter olabilir.

**Beğeni**

```http
POST /like
Authorization: Bearer <token>
Content-Type: application/json

{ "tweet": { "id": 1 } }
```

Yorumu beğenmek için `{ "comment": { "id": 1 } }` gönderilir. `/dislike` aynı gövdeyi kullanır.

## Proje Yapısı

```
src/main/java/com/example/demo
├── config        # SecurityConfig, JwtAuthenticationFilter
├── controller    # Auth, Tweet, Comment, Like controller'ları
├── dto           # İstek/yanıt nesneleri
├── entity        # User, Tweet, Comment, Like
├── exceptions    # ApiException, GlobalExceptionHandler
├── repository    # Spring Data JPA repository'leri
├── service       # İş mantığı katmanı
└── util          # JwtUtil, SecurityUtils, doğrulama yardımcıları
```
