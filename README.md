![](https://dpnw.site/assets/img/logo_white.png)

![](https://dpnw.site/assets/img/desc_card/dppcore.jpg)

## All DP-Plugins depend on the [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core) plugin. <br>Please make sure to install [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core)

## Discord
### Join our Discord server to get support and stay updated with the latest news and updates.

### If you have any questions or suggestions, please join our Discord server.

### If you find any bugs, please report them using the inquiry channel.

<span style="font-size: 18px;">**Discord Invite: https://discord.gg/JnMCqkn2FX**</span>

<br>
<br>

<details>
	<summary>korean</summary>

![](https://dpnw.site/assets/img/desc_card/desc.jpg)

## DP-AfkShop 플러그인 소개
DP-AfkShop은 잠수 포인트&상점 플러그인입니다.<br>
월드가드와 연동하여 원하는 구역을 AFK 구역으로 설정합니다.<br>
유저들은 해당 구역에서 일정 시간 머무르면 AFK Point를 획득할 수 있습니다.<br>
AfkShop을 만들어 유저들이 AfkPoint로 아이템을 구매할 수 있습니다.<br>

## 플러그인 특징
- **상점 GUI 편집**: 모든 잠수 상점은 GUI로 편집합니다.
- **잠수 포인트** : 잠수 구역에 머무르면 획득 가능합니다.
- **잠수 구역 설정**: 원하는 월드의 Region을 잠수 구역으로 설정할 수 있습니다.
- **시간당 포인트 설정**: 원하는 시간마다 원하는 포인트를 획득하도록 설정할 수 있습니다.
- **플레이스홀더 지원**: 잠수 포인트와 잠수 시간을 플레이스홀더로 확인가능합니다. 

## 플레이스홀더
- `%dpas_afkpoint%` : 플레이어의 잠수 포인트를 반환합니다.
- `%dpas_afktime` : 플레이어의 잠수 시간을 초 단위로 반환합니다.
- `%dpas_afktime_HMS` : 플레이어의 잠수 시간을 시/분/초 포멧으로 반환합니다.
- `%dpas_isInAfkArea` : 플레이어가 현재 잠수 구역에 있는지 여부를 반환합니다.

## 의존성
- DPP-Core
- PlaceholderAPI
- WorldGuard

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/cmd-perm.jpg)

## 명령어
### 관리자 명령어
| 명령어                                  | 설명                                                  |
|--------------------------------------|-----------------------------------------------------|
| `/dpafkshop create <name>`           | 잠수 상점을 생성합니다.                                       |
| `/dpafkshop delete <name>`           | 잠수 상점을 삭제합니다.                                       |
| `/dpafkshop items <name>`            | 잠수 상점의 아이템을 설정하는 GUI를 엽니다.                          |
| `/dpafkshop price <name>`            | 잠수 상점의 가격을 설정하는 GUI를 엽니다.                           |
| `/dpafkshop page <name> <page>`      | 잠수 상점의 페이지를 설정합니다. (기본 값 : 0)                       |
| `/dpafk setting <time> <point>`      | 잠수 포인트의 지급 주기와 지급량을 설정합니다. (시간 단위: s/m/h)           |
| `/dpafk point give <point> (player)` | 잠수 포인트를 지급합니다.                                      |
| `/dpafk point take <point> (player)` | 잠수 포인트를 차감합니다.                                      |
| `/dpafk point set <point> (player)`  | 잠수 포인트를 설정합니다.                                      |
| `/dpafk point clear (player)`        | 잠수 포인트를 초기화합니다.                                     |
| `/dpafk wgadd <region> (world)`      | 잠수 구역을 추가합니다. (월드를 입력하지 않으면 현재 플레이어의 월드를 기본으로 합니다.) |
| `/dpafk wgremove <region> (world)`   | 잠수 구역을 제거합니다. (월드를 입력하지 않으면 현재 플레이어의 월드를 기본으로 합니다.) |
| `/dpafk wglist`                      | 잠수 구역 목록을 확인합니다.                                    |
**주의사항**: 페이지는 0부터 시작입니다. 본인이 설정하고 싶은 최대 페이지에 -1을 한 값을 사용하십시오.
ex) 설정하고 싶은 최대 페이지 : 2 => 명령어 사용 : `/dpafkshop page test 1`

### 유저 명령어
| 명령어                             | 설명                                     |
|---------------------------------|----------------------------------------|
| `/dpafkshop open <name>`        | 잠수 상점을 엽니다.                            |

### 권한
- `dpas.open`: 잠수 상점 오픈 권한
- `dpas.create`: 잠수 상점 생성 권한
- `dpas.delete`: 잠수 상점 삭제 권한
- `dpas.items`: 잠수 상점 아이템 설정 권한
- `dpas.price`: 잠수 상점 가격 설정 권한
- `dpas.page`: 잠수 상점 페이지 설정 권한
- `dpas.setting`: 잠수 포인트 지급 주기 및 지급량 설정 권한
- `dpas.point`: 잠수 포인트 지급/차감/설정/초기화 권한
- `dpas.wgadd`: 잠수 구역 추가 권한
- `dpas.wgremove`: 잠수 구역 제거 권한
- `dpas.wglist`: 잠수 구역 목록 확인 권한

## 사용법 예시
- 잠수 상점 생성: `/dpafkshop create test`
- 잠수 상점 아이템 설정: `/dpafkshop items test`
- 잠수 상점 가격 설정: `/dpafkshop price test`
- 잠수 상점 페이지 설정: `/dpafkshop page test 1`
- 잠수 상점 삭제: `/dpafkshop delete test`
- 잠수 상점 오픈: `/dpafkshop open test`
- 잠수 포인트 지급 주기와 지급량 설정 : `/dpafk setting 30s 10`
- 잠수 포인트 지급 : `/dpafk point give 100 (player)`
- 잠수 포인트 차감 : `/dpafk point take 100 (player)`
- 잠수 포인트 설정 : `/dpafk point set 100 (player)`
- 잠수 포인트 초기화 : `/dpafk point clear (player)`
- 잠수 구역 추가 : `/dpafk wgadd afk (world)`
- 잠수 구역 제거 : `/dpafk wgremove afk (world)`
- 잠수 구역 목록 확인 : `/dpafk wglist`


</details>

<details open>
	<summary>english</summary>

![](https://dpnw.site/assets/img/desc_card/desc.jpg)
## DP-AfkShop Plugin Introduction
**DP-AfkShop** is an AFK (away-from-keyboard) point and shop plugin.  
It integrates with WorldGuard to designate specific regions as AFK zones.  
Players can earn AFK Points by staying within those zones for a certain period.  
Admins can create AfkShops where players can purchase items using their AFK Points.

## Plugin Features
- **Shop GUI Editing**: All AFK shops are managed through a graphical user interface (GUI).
- **AFK Points**: Players earn points by staying in AFK zones.
- **AFK Zone Settings**: Designate any WorldGuard region as an AFK zone.
- **Customizable Point Timing**: Configure how many points are given at what time intervals.
- **Placeholder Support**: Track AFK points and time using placeholders.

## placeholders
- `%dpas_afkpoint%` : Returns the player's AFK points.
- `%dpas_afktime` : Returns the player's AFK time in seconds.
- `%dpas_afktime_HMS` : Returns the player's AFK time formatted as hours/minutes/seconds.
- `%dpas_isInAfkArea` : Returns whether the player is currently in an AFK zone (true or false).

## Dependencies
- DPP-Core
- PlaceholderAPI
- WorldGuard

<br>
<br>

## Commands
### Admin Commands
| Command                                 | Description                                                                 |
|----------------------------------------|-----------------------------------------------------------------------------|
| `/dpafkshop create <name>`             | Creates a new AFK shop.                                                    |
| `/dpafkshop delete <name>`             | Deletes the specified AFK shop.                                            |
| `/dpafkshop items <name>`              | Opens the item-setting GUI for the specified AFK shop.                     |
| `/dpafkshop price <name>`              | Opens the price-setting GUI for the specified AFK shop.                    |
| `/dpafkshop page <name> <page>`        | Sets the page for the specified AFK shop (default: 0).                     |
| `/dpafk setting <time> <point>`        | Sets the point interval and amount. (Time units: s/m/h)                    |
| `/dpafk point give <point> (player)`   | Gives AFK points to a player.                                              |
| `/dpafk point take <point> (player)`   | Deducts AFK points from a player.                                          |
| `/dpafk point set <point> (player)`    | Sets a player's AFK points.                                                |
| `/dpafk point clear (player)`          | Resets a player's AFK points.                                              |
| `/dpafk wgadd <region> (world)`        | Adds an AFK zone. If no world is specified, uses the player's current world. |
| `/dpafk wgremove <region> (world)`     | Removes an AFK zone. If no world is specified, uses the player's current world. |
| `/dpafk wglist`                        | Lists all registered AFK zones.                                            |

**Note**: Pages start at 0. To set the max page, use your intended value minus one.  
Example: Desired max page = 2 → Command: `/dpafkshop page test 1`

### User Commands
| Command                      | Description                        |
|-----------------------------|------------------------------------|
| `/dpafkshop open <name>`    | Opens the specified AFK shop.      |

### Permissions
- `dpas.open`: Permission to open AFK shops
- `dpas.create`: Permission to create AFK shops
- `dpas.delete`: Permission to delete AFK shops
- `dpas.items`: Permission to configure shop items
- `dpas.price`: Permission to set item prices
- `dpas.page`: Permission to set shop pages
- `dpas.setting`: Permission to configure point interval/amount
- `dpas.point`: Permission to give/take/set/clear AFK points
- `dpas.wgadd`: Permission to add AFK zones
- `dpas.wgremove`: Permission to remove AFK zones
- `dpas.wglist`: Permission to list AFK zones

## Usage Examples
- Create AFK shop: `/dpafkshop create test`
- Set shop items: `/dpafkshop items test`
- Set shop prices: `/dpafkshop price test`
- Set shop page: `/dpafkshop page test 1`
- Delete AFK shop: `/dpafkshop delete test`
- Open AFK shop: `/dpafkshop open test`
- Set point interval and amount: `/dpafk setting 30s 10`
- Give points: `/dpafk point give 100 (player)`
- Take points: `/dpafk point take 100 (player)`
- Set points: `/dpafk point set 100 (player)`
- Clear points: `/dpafk point clear (player)`
- Add AFK zone: `/dpafk wgadd afk (world)`
- Remove AFK zone: `/dpafk wgremove afk (world)`
- List AFK zones: `/dpafk wglist`
</details>

<br>
<br>
