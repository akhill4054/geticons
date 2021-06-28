# GetIcons
Unofficial icon-finder app that shows public icons from publishers around the globe.

## Features
### Home Screen <span id="home-screen"></span>
- **Icon Set Tab** - Shows a list all the available public icon-sets, this is an endless scrollable list where 20 items are fetched at a time. 
  - Clicking on an item takes you to IconSetDeatails screen. 
  - Clicking on info button on an item will show you addition information about the icon-set. License text is clickable (if available), cliking on it opens license url with the device web broswer.
- **Icon Search Tab** - Shows you an endless list of all public icons where 20 items are fetched at a time. 
  - If you type anything in the search bar, the list will show items of the search result. 
  - You can also filter the list by premium icon type and license type.
  -  Clicking on any of the list icons takes you the [IconDetails](#icon-details) screen. 
  -  You can download the free icons, clicking on download button on a list item will open a [bottom-sheet dialog](#icon-download) showing the download options.
<p align="center">
  <img height="350px" alt="home_public_iconsets" src="https://github.com/akhill4054/geticons/blob/master/screenshots/home_public_iconsets.jpg"> &nbsp; &nbsp; &nbsp; &nbsp;
  <img height="350px" alt="home_search_icons" src="https://github.com/akhill4054/geticons/blob/master/screenshots/home_search_icons.jpg"> &nbsp; &nbsp; &nbsp; &nbsp;
  <img height="350px" alt="icon_search_filters" src="https://github.com/akhill4054/geticons/blob/master/screenshots/icon_search_filters.jpg">
</p>

### Icon-Set Details <span id="icon-set-details"></span>
Shows details about an icon-set containing, name, type, price, author/user, website, license, and a list of all the icons under the icon-set(NOTE: pagination on this list is not available as the API to fetch icons under an icon-set doesn't seem to support it or maybe it's broken at the moment. There is a url param called offset for it, but it doesn't seem be working, so I decided to remove the pagination from this list.).
- Icons on the list are downloadable(if free), and clicking on them will take you to the [IconDetails](#icon-details) screen.
- Clicking on author name will to the [AuthorDetails](#author-details) screen.
- Website url is clickable (if available).
  
<p align="center">
  <img height="350px" alt="iconset_details" src="https://github.com/akhill4054/geticons/blob/master/screenshots/iconset_details.jpg">
</p>

### Icon Details <span id="icon-details"></span>
Shows details about an icon's icon-set containing name, type, price, author/user, website, license as (as same as details on the [IconSetDetails](#icon-set-details) screen) with an option to download the icon (if free), which open a [bottom-sheet dialog](#icon-download) showing the download options.
<p align="center">
  <img height="350px" alt="icon_details" src="https://github.com/akhill4054/geticons/blob/master/screenshots/icon_details.jpg">
</p>

### Author Details <span id="author-details"></span>
Shows some basic details about an author listing a list of author's icon-sets with pagination where 20 items are fetched at a time.
- Clicking on an item open the [IconSetDetails](#icon-set-details) screen.
<p align="center">
  <img height="350px" alt="author_icon_sets" src="https://github.com/akhill4054/geticons/blob/master/screenshots/author_icon_sets.jpg">
</p>

### Icon Download <span id="icon-download"></span>
Shows selectable options, listing all the available download formats for an icon.
- The start download button downlods the selected/checked icon formats.
<p align="center">
  <img height="350px" alt="icon_download" src="https://github.com/akhill4054/geticons/blob/master/screenshots/icon_download.jpg">
</p>

### Other
- [HomeScreen](#home-screen) has an option to switch between day/night modes. Light mode looks as follows -
<p align="center">
  <img height="350px" alt="light_mode_ui_1" src="https://github.com/akhill4054/geticons/blob/master/screenshots/light_mode_ui_1.jpg"> &nbsp; &nbsp; &nbsp; &nbsp;
  <img height="350px" alt="light_mode_ui_2" src="https://github.com/akhill4054/geticons/blob/master/screenshots/light_mode_ui_2.jpg">
</p>
- If an API call fails due to a network error, you'll see an error message with a retry button.

### Decisions Made During the Development
- Items on the icon list don't show license and author details as they're not provided by the APIs to list icons.
- Removed swipe to refresh from the lists on the [HomeScreen](#home-screen), as it was causing some scroll issues.
- Networks call are written in a synchronous way using Kotlin-corutines instead of conventional callback pattern, I just wanted to give this approach a try. This approach makes your codes more readable and easy to manage though.
- Almost all fields of models classes are marked with Gson's @SerializedName annotation, doing so isn't really necessary but I still decided to do it to prevent the problems that could be caused by accidental field renames.
