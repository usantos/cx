# Classic Home: cart, payment notice, notification state audit

Reference source: `/Users/ubiracysantos/Projects/legado/simlo-app-android/app/src/main`.

This is a source-based audit. Device rendering and test execution must be recorded separately by the integration run; the changes below do not establish screenshot pixel equality.

| State | Legacy reference / behavior | Compose mapping / validation |
| --- | --- | --- |
| Empty / zero / negative cart total | `view/fragment/SomadorCarrinhoFragment.java::showFragmentSomadorCarrinho`: view GONE unless total > 0, regardless of count | `isCartSummaryVisible` and Home summary guard agree. Added Home UI regression for zero total with nonzero count and negative total. |
| Populated cart | `fragment_somador_carrinho.xml`: 36dp bar, amount 16sp, caption 12sp, 28dp cart, 16dp badge | Existing shared summary transcribes these dimensions and background paths. Shared component unchanged. Source-verified; no new rendered comparison. |
| Count boundary | Fragment uses list size through 999, `mais_999` (`...`) above 999 | Existing shared formatting agrees; added Home transition 999 -> 1000. Count means complete snapshot, not grouped cards. |
| Total update | Fragment formats Brazilian currency | Existing shared Brazilian currency formatter; added Home displayed amount assertion `R$ 25,50`. |
| Cart tap | `PrincipalActivity` attaches `vaiProCarrinho` to summary container and opens cart when visible | Home delegates `actions.openCart`; added callback assertion. Authentication/navigation contract remains owned by the route. |
| Visitor / logged-in source | Fragment reads local cart for visitor; remote with local fallback for authenticated user | Summary remains stateless and uses host snapshot. Domain/auth/data paths intentionally unchanged by this UI patch. |
| Payment not identified | `PrincipalActivity::trataPopupDeNotificacaoHandler` prioritizes unidentified-payment notice over latest notification | `HomeRouteDialogs` and notification coordinator retain this priority. `HomePaymentNotice` already uses exact `alerta_descricao_pagamento_nao_identificado` text and purchase CTA. Existing payment UI and ViewModel tests cover action/state. |
| Payment notice dismissal | `DialogUtils::dialogLabelUmBotaoListener` uses blocking one-button dialog | Existing design-system alert disables back/outside dismissal. Source match; global dialog geometry was not changed. |
| Notification absent / payment present | Legacy does not present a notification if absent or payment is unidentified | Existing route guards agree. Source-verified. |
| Notification without link | `nova_popup_notificacao.xml` / `NotificacaoUtils`: close action only; literal title/content | Existing popup behavior/tests retain literal text and hide open-link action. |
| Notification with link | Utils reveals link button and places close left/open right; opening link does not dismiss popup | Existing callback behavior/test agrees. |
| Notification image | Utils decodes base64 or retains placeholder after exception | Existing decoder preserves fallback and bounds allocation off UI thread; decoder tests retained. |
| Notification panel | XML reserves **50dp side margins**, with top/bottom guideline bounds at 15% / 85%, scrollable content | Corrected Home-specific panel from shared modal margins (24dp/max560) to fixed 50dp margins. Added long-content geometry and scroll-to-actions test, including <=70% viewport height. |

## Consumer impact

`CaixaDSCartSummary` also serves betting combo detail/list, marketplace/filter, confirmed bets, design-system catalog and app tests. No shared cart implementation or tokens were changed. Notification width is scoped to `HomeNotificationPopup`; other dialogs retain their existing sizing.

## Files changed for this audit

- `HomeNotificationPopup.kt`: fixed legacy side margins and geometry test tags.
- `HomeNotificationPopupTest.kt`: long-content panel width/height/scroll regression.
- `HomeCartSummaryUiTest.kt`: Home visibility, amount/count updates and tap regression.

No Gradle or adb commands were executed by this audit worker. Build, emulator tests and screenshots are delegated to the parent integration run to avoid concurrent Gradle/device mutation.
