# uwasa（ウワサ）

匿名性を重視したコミュニティサービスです。
自宅サーバー上で実際に稼働しています。

🔗 **https://uwasa.todtnwater.com**

[日本語](#日本語) | [한국어](#한국어)

---

## 日本語

### 概要

ユーザーが気軽に投稿し、リアクションでき、更新がリアルタイムに反映されるコミュニティサービスです。
開発からサーバー構築・デプロイ・運用までをすべて一人で担当しました。

### 主な機能

- 投稿の作成・閲覧
- リアクション機能
- SSE（Server-Sent Events）によるリアルタイム更新
- 会員登録・ログイン（Spring Security、BCryptによるパスワードのハッシュ化）
- マイページ
- お問い合わせ機能
- 管理者画面（ダッシュボード、通報管理、お問い合わせ管理、IPブロック）
- 利用規約・プライバシーポリシーページ

### 技術スタック

| 区分 | 技術 |
|------|------|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Database | Oracle Database XE 21c（Docker） |
| Infra | Ubuntu Server, systemd, Cloudflare Tunnel, Tailscale |

### システム構成

### 工夫した点

- **サーバーの分離**：WebサーバーとDBサーバーを別マシンに分け、DBはTailscaleのプライベートネットワーク経由でのみ接続するようにしました。
- **機密情報の管理**：DB接続情報は `application-secret.properties` に分離し、Git管理対象から除外しています。
- **自宅サーバーでの運用**：Windows ServerからUbuntu Serverへ移行し、systemdによる自動起動・常時稼働を構成しました。ポート開放をせず、Cloudflare Tunnelで公開しています。
- **荒らし対策**：管理者画面から特定IPをブロックできる機能を実装しました。

### ローカルでの実行方法

1. リポジトリをクローン

```bash
git clone https://github.com/todtnwater/uwasa.git
cd uwasa
```

2. DB接続情報ファイルを作成

```bash
cp src/main/resources/application-secret.properties.example src/main/resources/application-secret.properties
```

作成した `application-secret.properties` に、ご自身のDB接続情報を入力してください。

3. 実行

```bash
./mvnw spring-boot:run
```

`http://localhost:8083` でアクセスできます。

---

## 한국어

### 개요

익명성을 중시한 커뮤니티 서비스입니다. 게시글 작성, 리액션, SSE 기반 실시간 갱신을 지원하며, 개발부터 서버 구축·배포·운영까지 혼자 담당했습니다. 현재 자택 서버에서 실제로 운영 중입니다.

### 주요 기능

- 게시글 작성·조회, 리액션
- SSE(Server-Sent Events) 실시간 갱신
- 회원가입·로그인 (Spring Security, BCrypt 비밀번호 해시)
- 마이페이지, 문의 기능
- 관리자 화면 (대시보드, 신고 관리, 문의 관리, IP 차단)

### 기술 스택

Java 17 / Spring Boot / Spring Security / Spring Data JPA / Thymeleaf / Oracle XE 21c (Docker) / Ubuntu Server / systemd / Cloudflare Tunnel / Tailscale

### 포인트

- 웹 서버와 DB 서버를 분리하고, DB는 Tailscale 사설망으로만 접속
- DB 접속 정보는 별도 파일로 분리해 Git에서 제외
- Windows Server → Ubuntu Server 이전, systemd로 상시 구동, 포트 개방 없이 Cloudflare Tunnel로 공개
- 관리자 화면에서 IP 차단 기능 구현
