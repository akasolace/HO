*Please visit the HO! forum in the CHPP section of Hattrick if you have any questions!
If you find a bug, please open an issue on [GitHub](https://github.com/akasolace/HO/issues)*

# Changelist HO! 5.0

## Some numbers:
  - xxx commits
  - xxx files changed (xxx additions and xxx deletions)
  - xxx issues closed
  - xx contributors


## Highlights
  - complete makeover of the statistics module (#788)
  - improving consistency of dark themes
  - preview of new module youth academy (#367)



## Detailed Changelog

### GUI
  - theming of training bar indicators

### Finance
  - all information relative to Finance are now stored in the database (#793)


### Statistics
  - complete makeover of the statistics module: more data, more visible .... (#788)

### Squad

  - Separate rating and match date columns for easier sorting. (#895)
  - adjusting of experience subskill is displayed in player details panel (#1023) 


### Team Analyser
  - tuning simulation (#865)
  - fix npe if system configurations of team analyzer are not available in database (#956)


### Rating
  - new default model

### Matches
  - new match location filter (home/away/neutral/all) on Statistics tab (#470)
  - fix display of national team matches (#1048)
  - fix team analyzer's display of national team matches (#1049)

### Lineup
  - Match orders remove substitution limit (#856)
  - complete revamp of lineup panel
  - Separate rating and match date columns for easier sorting. (#895)


### IFA


### Training
  - many bug fixes
  - fix training recap table's resetting scroll positions on table refresh  (#1011) 
  - experience increase of new national hto matches (#1089)
  


### Misc
  - fix issue with user preferences (incl. OAuth access token) not being saved upon exiting HO. (#811)
  - improved handling of timezone accross the app
  - sanitizing of preferences panel
  - automatic check for new HO! version after HRF-Download (#1019)
    - configurable in File -> Preferences -> Release Channels
  - fix NT team players' name download (#1025)


### League



## Translations
  - HO! is currently available in xxx languages thanks to the work of xxx translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):




# Changelist HO! 4.1

  - fixes NPE events in module Player Analysis and SpecialEvents (#797)



# Changelist HO! 4.0

## Some numbers:
  - 277 commits
  - 794 files changed (20'997 additions and 21'491 deletions)
  - 108 issues closed
  - 9 contributors


## Highlights

  - New build process:
    - **HO! now requires Java 14 but artefacts including JRE are made available**
    - **in-app upgrade is now supported**
    - directory structure has changed (db location, log files, ...)
   - [FEAT] Improved GUI, new themes, and implemented dark mode. #85
   - [FEAT] Complete rewrite of Special Events module


## Detailed Changelog


### Squad

  - [FIX] Remove lag in sync between tables in Squad #465
  - [FIX] Provide better default for initial position of player comparison divider.

### Team Analyser

 - [FEAT] Save adjusted lineup ratings of MatchPredictionPanel in an extra row #66

### Rating

  - [FIX] Fixes Long Shots tactic level calculation

### Matches

  - [FIX] Some icons fixes in match report
  - [FEAT] Information about extra time or penalty shoot out in match result #561

### Lineup

  - [FEAT] Provide better defaults for initial position of dividers.
  - [FEAT] Edit man marking match order [#660]
  - [FIX] Fix bug in download of man marking match orders [#632]
  - [FIX] Fix bug in upload of position change match orders [#633]

### IFA

  - [FIX] add supports for 9 new leagues: Belize, Madagascar, Botswana, Saint Vincent and the Grenadines, Myanmar, Zambia, San Marino, Puerto Rico and Haiti [#539]


### Training

  - [FIX] Fix bug of season correction calculation. Training effect table shows wrong seasons in week 16 [#539]
  - [FIX] Subskill recalc takes into account training that took place before the first hrf download [#512]
  - [FIX] Fix bug of training effect of Walkover matches [#623]
  - [FEAT] Individual training plans for each player in training preview [#587]

### Misc

  - [FEAT] Remove jcalendar dependency.
  - [FIX] ExperienceViewer removed [#503]
  - [FIX] Avoid potential infinite loop at startup. [#584]
  - [FIX] Layout issue in multiple screen setup. [#618]
  - [FEAT] Remove all printing functionality.
  - [FEAT] Remove player state colour, and display icons instead in Lineup.
  - [FEAT] Refactor progress bar display [#722]

### League

  - [FEAT] Make Promotion status more visible. [#521]

## Translations
  - HO! is currently available in 35 languages thanks to the work of 78 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):


| Contributor       | Translations | Percentage |
|-------------------|--------------|------------|
| Kimmo             | 843          | 48.03      |
| Andreas           | 745          | 42.45      |
| \_KOHb\_            | 436          | 24.84      |
| Moorhuhninho      | 409          | 23.3       |
| TeamBMW           | 349          | 19.89      |
| Csaba             | 230          | 13.11      |
| André             | 207          | 11.79      |
| h3t3r0            | 139          | 7.92       |
| asteins           | 79           | 4.5        |
| Hakkarainen       | 62           | 3.53       |
| DavidatorusF      | 61           | 3.48       |
| Manny             | 60           | 3.42       |
| Baler0            | 54           | 3.08       |
| Stef              | 46           | 2.62       |
| dzsoo             | 44           | 2.51       |
| Bartosz           | 41           | 2.34       |
| murko             | 39           | 2.22       |
| Volker            | 38           | 2.17       |
| sich              | 38           | 2.17       |
| Sebas90           | 32           | 1.82       |
| akasolace         | 31           | 1.77       |
| Lidegand          | 29           | 1.65       |
| Saleh             | 14           | 0.8        |
| LeSchmuh          | 11           | 0.63       |
| Ricardo           | 6            | 0.34       |
| Motavali          | 5            | 0.28       |
| karelant.         | 4            | 0.23       |
| Philipp           | 2            | 0.11       |
| Sumame.           | 2            | 0.11       |
| RaV               | 1            | 0.06       |
| Total translated: | 4057         | 6.42       |



# Changelist HO! 3.1

## Some numbers:
  - xxx commits
  - xxx files changed (xxx additions and xxx deletions)
  - xx issues closed
  - xx contributors

## Highlights

## Detailed Changelog

### Lineup
- [FIX] substitute player can now occupy more than one bench position #506



# Changelist HO! 3.0

## Some numbers:
  - 90 commits
  - 293 files changed (11,027 additions and 6,341 deletions)
  - 42 issues closed
  - 4 contributors


## Highlights


  - in case of promotion/demotion information about the new league will be available as soon as last game of the season has been played

  - impact of special events on score for both you and your opponent based on latest lineup information

  - new match report mocking HT full report

  - full control on which game to download (e.g. exclude HTO integrated games)


## Detailed Changelog

### Download

- [NEW] Full control on which game to download (e.g. exclude HTO integrated games)  #290


### Squad

  - [FIX] ordering of best position is now saved on closing #397
  - [NEW] manual adjustment of experience level (similar to other skills) #463
  - [FIX] Fix team summary comparison after new download from HT #475


### Team Analyser

 - [NEW] impact of special events on score for both you and your opponent based on latest lineup information #299
 - [FIX] fix calculation of HatStats and Loddar in TeamAnalyzer #464

### Rating

 - [NEW] impact of match orders on rating is now directly visible from the subsitution tab, helping finding the best time for subsitution change  #294
 - [FIX] all match orders are now consider in minute-bases prediction rating (behaviour change, position wap ...). Until now only substitution were considered #385
 - [FIX] counterattackrating calculation beyond divine has been fixed #398


### Matches

- [NEW] new match report mocking HT full report #421
- [FIX] Matches Overview NPE #396
- [FIX] NPE when trying to simulate upcoming games #472
- [FIX] Improved performance when first displaying the tab when the database contains lots of matches #471
- [FIX] Improved performance when exiting HO when the database is big #471
- [NEW] removed Match Analyzer module #477


### Lineup

- [FIX] to improve visibility, only last name is displayed in combo box, but the full player name is available via infobull on mouse hovering  #394
- [FIX] player name column is now resizable  #382


### Training

- [NEW] Training tab now shows information about skill devaluation (this can be disable in preference) #286
- [FIX] Training history table skill values of players with more than one skill devaluations fixed #444
- [FIX] fix an issue when new player had a skill increase on first training and before it was ever imported into HO! #188


### Misc

   - [FIX] player age calculation is now correct #422
   - [FIX] Index Out Of Bounds Exception at startup on new DBs #448
   - [FIX] fix multiple display issues about player names (composed name, nicknames)  #451
   - [FIX] removed deprecated training block feature  #486


### League

   - [NEW] in case of promotion/demotion information about the new league will be available as soon as last game of the season has been played #247
   - [FIX] HO! will now automatically recognize league change at the begining of the season #391
   - [FIX] Fix position arrows colour in series table #499


## Translations
  - HO! is currently available in 36 languages thanks to the work of 56 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):


| username       | nb translated terms |
|----------------|:-------------------:|
| Lidegand       | 378                 |
| Saleh          | 65                  |
| Mauro Aranda   | 62                  |
| sich           | 58                  |
| mondstern      | 40                  |
| h3t3r0         | 9                   |
| murko          | 6                   |


# Changelist HO! 2.1

## Some numbers:
  - 350 commits
  - 903 files changed (51,839 additions and 34,085 deletions)
  - 8 contributors


## Documentation
 - Documentation moved to [Read the docs](https://ho.readthedocs.io/)


## GUI
 - New icons across the app: specialty, match type, match events, ...

## Download
- Warning when "not possible to fetch data at the moment because this team is currently playing a game" #291


## Player details
  - Best positions are highlighted #213
    - In the preference (bottom of tab “Misc”) you can set the tolerance level to highlight the player's alternative positions
  - Harmonization of absolute vs relative player contribution/performance #282
      - In squad and Lineup, are calculated both the absolute and the relative contribution of a player to the ratings
        - The absolute is more or less what would be the sum of his contribution to the 7 sectors
        - The relative contribution helps to determine the best position for a given player. It is the absolute contribution corrected by a factor. This allows accounting for that some positions contributes more to the total ratings than others
  - In various tab, allow sorting by position and ratings #293
  - Fix decimal negative difference #295


## Lineup
  - Fix set-pieces taker slot when reloading saved match lineup  #316
  - Fix “exclude last Lineup” when doing manual lineup #328


## Rating
  - Fix a bug that was causing faulty ratings  #292
  - Fix Hatstats and Loddarstats in Teamanalyser #321


## Matches
  - Division battle match icon + game specifics #284 #310 #313
  - Fix "show my cup games" #320 #323 #326
  - Match Highlights: complete rewrite - full support of all match events  #344
  - Match modules, cleaning of statistics tabs #355


## NT
  - Extended style of play options #191


## Training
  - Training week preview in Training Tabs #234
  - Stamina training preview  #125 #311
  - Fix training for secondary cups #306
  - Track experience acquisition #199
  - Fix training forecast displaying wrong skill up #368
  - Fix the trainee marking of future training plan #296


## Misc
  - Out of bound exception for Hall of Fame players #350
  - Summary line in player overview presenting team value (avg TSI, total TSI, avg form, ….) #373
  - Team analyzer: Fix HatStats and LoddarStats always displaying zero #324
  - Fix MatchDayPanel download button not available while the match is over #330
  - Fix MatchDayPanel null pointer exception #337
  - Fix a bug when downloading data while having a generation match planned #365
  - Team analyzer: Fix display issue in case of a team walkover #377


## Translations
  - HO! is currently available in 36 languages thanks to the work of 45 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):


| username       | nb translated terms |
|----------------|:-------------------:|
| sich           | 524                 |
| Moorhuhninho   | 332                 |
| Boy            | 193                 |
| Saleh          | 185                 |
| Motavali       | 181                 |
| Sergejs        | 114                 |
| TeamBMW        | 81                  |
| Juan           | 67                  |
| Lidegand       | 56                  |
| Globe96        | 46                  |
| Zigmas         | 45                  |
| dzsoo          | 44                  |
| murko          | 38                  |
| Baler0         | 28                  |
| taimikko       | 21                  |
| Ricardo        | 18                  |
| DavidatorusF   | 17                  |
| brokenelevator | 3                   |
| Massimo        | 3                   |


# Changelist HO! 2.0

## Added
  - Menu
    - checks software update and changelog under help #82 (Windows / Linux you can upgrade to the next version of HO using the update function)
    - check the release channel you want use (DEV/BETA/Stable)
    - **according to the channel version used, you will see the new logo and banner #84**

  - Lineup
    - **Add new lineup substitutes (with wingback and extra) #120 #105**
    - **Blue border on your trained player #44**
    - **Green mark near player name for minutes based training according to the one chosen under training tab #44**
    - Fix for translation in the menu from style of play #119
    - Display only players lastname to improve lisibility #126
    - Better use of space to improve visibility #133
    - Remove broken/useless buttons #132
    - Support for download of non official Lineups (fixed in 1437)
    - Grey background for player on the field or on the bench #146
    - Hatstats and loddastat added to ratings table

  - Rating
    - **Timeline allowing to see rating evolution over time**
    - **re-introduce [User Feedback](https://github.com/akasolace/HO/wiki/FeedBack) in order to allow among other things prediction rating improvement #38 #131**
    - **Formulas will not use anymore the stars but absolutes and relatives players evaluation’s value #212**
      - In squad and Lineup, are calculated both the absolute and the relative contribution of a player to the ratings
      - The absolute is more or less what would be the sum of his contribution to the 7 sectors
      - The relative contribution helps to determine the best position for a given player. It is the absolute contribution corrected by a factor. This allows to account for that some position contribute more to the total ratings than others (e.g. a player being divine in all his carac will contribute more to ratings if positioned as MidField than if positioned as WB)
      - The coeffs are calculated using the prediction parameters of HO. They are approximate but hopefully they would be improved if we got sufficient contribution from the newly introduce FeedBack plugin.
    - **Take weather event in consideration #111**
    - **Take substitution in consideration**
    - Improved formula #110
    - Improve prediction rating formula for t=0 #39
    - Improve prediction stamina effect #40
    - **Offer rating comparison HT vs HO for t=0 from upload/download lineup tool**
    - Rating Charts with graph



 - NTHRF: Module is removed, now you can add your NT team in your own HO - see [wiki](https://github.com/akasolace/HO/wiki/NTHRF-since-HO-2.0)

 - Misc
   - **Support for all kind of matches #21**
   - **training calculation is now minute based #27**
   - Improvement to export CSV tools #42
   - IFA: New countries added #17 #262
   - Matches: possibility to download missing matches #237
   - Statistics Arena: added information about incoming Cash #49 #109
   - Squad: change age from year.day to year(day) #113






## Fixed
 - **startup issue for certain Linux configuration (#102)**
 - **Standard shortcuts for copy/cut/paste in MacOSX #177**
 - **training: fix for red card’s player, they had full training #127**
 - fix on money symbols #93
 - sorting issue after change filter in Arena section
 - bug due to match report size larger than db threshold
 - invalid select statement and duplicated IDs in matches tab
 - review HO build (Sanitize HO jar and fixes deb and rpm packages) #136 #139
 - Fix for properly import training from previous version #100
 - Error message instead exception when receive bad xml #140
 - Some database fixes


# Changelist HO! 1.436

## Fixed
 - optimized build is saving approximately 35% disk space on produced binaries (bug #71)
 - Preference|Check had lot of non-working entries: it has been deleted:
     - preference for HRF location save dialog has been moved into Preference|Download tab
     - preference regarding automatic version check has been moved  into Preference|Release Channels
       but is temporary deactivated until new behaviour is validated
 - ability to start HO on Linux from script after fresh install (bug #34)
 - version in splash screen and main GUI header (bug #36)
 - missing icon (bug #37)
 - missing columns in certain languages because of duplicate names (Player Analysis Tab) (bug #41)
 - poor quality shirt numbers on MacOS
 - wrong data in csv player export tool  (bug #42)
 - layout changes in transfer history tab
 - fired player transfer history in no longer removed on transfer history update
 - player data is no longer missing from transfer history top panel  (bug #45)
 - wrong link in menu bout HO!  (bug #43)
 - small bugs related to update process on all platforms (bug #57)
 - wages/prices are formatted as team local currency as opposed to host system currency (bug #93)

## Added
 - possibility for user to select and save release channel preference (DEV/BETA/STABLE)
 - display release notes before HO update
 - translation in csv player export
 - support for new HT layout by HT Copy Button in Transfer Scout
 - team transfer update now includes individual histories
 - information that player has been fired in transfer history panel
 - remove transfer button in transfer history panel
 - include direct link to download now version for mac user

## Changed
 - moved 'save HRF dialog' option to download tab (#80)
 - moved (and disabled) legacy 'update HO on download' option to release channel tab (#80)
 - appearance of release channel tab


Changelist HO! 1.435
====================

## Fixed
- bug related to the introduction of Support specialty
- bug related to Divine+ ratings in Match Analysis
- a long time bug where IFA tab would crash under certain circumstances of db update
- bug in Misc Tab where some values were not displayed
- bug where FW normal value was displayed in PlayerCompare (TAB Player Analysis)

##Added
- macOS version is working again
- statistics tab displays crowd numbers in each stadium section, including percentages
- HatStats statistics are now displayed in Matches module
- flags for Comores, Guam, Sri Lanka, Sao Tomé-et-Principe and Curaçao

##Removed
- Update menu for flags, prediction rating and translation files


Changelist HO! 1.434
====================
- Added check if database folder is writeable
- Removed market value logic (EPV)
- Added new staff structure to miscellaneous tab
- Added new staff assistant level to training tab
- Made sure staff assistant level is visible even if there are no staff at all.
- NThrf should be working again
- Added team name to title bar
- Modified training factors, including base week. Hopefully predictions are improved.

- Changes done related to Style of Play being introduced.
- Max number of orders is dependent on Tactical Assistant level.
- A warning appears if you somehow have more orders than allowed to.
- Added download and storage of style of play to match orders.
- Caution: style of play is always 0 if you haven't set a new default lineup for the upcoming match.
- Added support in lineup details (right panel).
- A combo box near the top to select style of play.
- The available choices depend on trainer type and Tactical Assistant level.
- A combo box near trainer choice to set a different number of tactical assistants in this panel only.
- Rating calculation uses style of play. The calculation uses a linear fit between the set values for
	offensive, neutral and defensive trainer contributions.
- Added upload of style of play to the match orders.
- Two new version of rating prediction values according to changes for the new season. Default and newB.
	Every feedback which one you think is better is appreciated.
- Added style of play support in matches and matchesanalyzer module.

- Increased training speed influence of assistant coaches from 3.2% to 3.5%.
- Added subskill import from hrf's from hattrickportal.pro (big thanks to duke for the support).
- Added a lineup and lineup with ratings export to HT-ML (nice for the forum).
- Added age column to player analysis module which displays age on that match date (skills are still the current).
- Show player compare tab under player analysis.
- Made authorization always ask for priviliges to set match orders.
- Reduced max assistant level from 15 to 10 after removal of one assistant position

- Added support for Divine+ ratings in various spots. Included are matches panel and team analyzer.
- Added "no coach" support for NThrf. National teams get a default trainer assigned.
- Made a fix so that one download cancel should be enough when importing hrf to empty database.
- Added support for third team.
- Fixed match report to display extra time.
- Fixed IFA to handle HT International league that has no country ID.
- Fixed match analyzer to handle tactics above divine.

Language updates:
-----------------
- Updated Danish translations (thx Max_Schreck).
- Major update to the Czech file (thx vik-m).
- Updated Lithuanian translations (thx LA-MJ).
- Updated German translations (thx-GM-Romanolus).
- Updated French translations (thx davidatorus).
- Updated Spanish and Spanish sudamericano translations (thx Baler0).
- Updated Portugues translations (thx a_cardoso).
- Updated Romanian translations (thx DragosE).
- Updated Hangul (Korean) translations (thx SI-CH).
- Updated Galego/Galician translations (thx LA-OReiDasSachadas).
- Updates up to forum post [post=14978456.685]. Thanks to numerous users.



Changelist HO! 1.433
====================
- Added support for second teams. They need their own HO database.
- Updated training speeds for shooting (thx Zoldon).
- Bugfix for wrong player parsing in Transferscout for norwegian users (thx Avec2).
- Bugfix for order changes not being loaded correctly in saved lineups.
- IFA: Added modules menu option to rebuild the match database.
- Language files are now UTF=8, and should be more readable.
- Changed the menu link to the new user guide.

Language updates:
-----------------
- Updated French translations (thx davidatorus).
- Updated Hangul (Korean) translations (thx SI-CH).
- Updated Hebrew translations (thx Mod-Transy).
- Updated Italiano translations (thx bigpapy).
- Updated Lithuanian translations (thx LA-MJ).
- Updated Norsk translations (thx ksunmn).
- Updated Portugues translations (thx a_cardoso).
- Updated Svenska translations (thx montevi, murko).


Changelist HO! 1.432
====================
- Hattrick Organizer now requires Java 1.6 (aka JRE6). Open JDK supports older operating systems that oracle does not. Please update your Java installation if needed.

- Player subskills
	. Training is now minute based. Correct counting only for matches downloaded in 1.431 or later.
		It is possible to delete your own matches by the db cleanup tool, and download them again by
		archive options in download. After this a full subskill recalculation could be interesting.
	. Osmosis training has been added. A small training effect for all players on the field.
	. Added green background for 100% trainees and yellow for 50% trainees in training prediction (white background for osmosis training).
	. Use subskill overflow for training prediction to avoid rounding errors hiding coach and assitant number changes effects.
	. The skill level effect on training has been adjusted.
	. Skill drops due to age at 30 or above, or at skills of 16 or above is in as a first version.
		Speeds will require more or less tuning.
	. Training offset has been removed, and been replaced with an option to adjust the subskill directly.

- Team Analyzer
	. All update restrictions have been removed, including favorite teams.
	. Update will now download the matches to fill out your autofilter request, or the 30 last opponent matches if a manual filter is selected.
	. Tournament opponents are in, and so are tournament matches.
	. Auto filter options are now saved when you analyze, and should reappear that way until changed.
	. Auto filter options for tournament and masters matches.
	. Fixed bug that renaming team after the last league match prevented analyzing matches before name changes.

- Match order uploads
	. The option to upload match orders to Hattrick have been added.
	. This is a Hattrick supporter feature, and non-supporters will find the upload button inactive.
	. The lineup tab now has its own tabs, the new ones are:
		. Lineup - the old one.
		. Match orders - a place to set and view match orders.
		. Penalty takers - a place to view and set the penalty shooter list.
		. Upload/Download - a place to upload and download orders to and from specific matches.

- Special Events
	. got a lot of improvments (esp. the filter)

- Lazy initialization/refresh
	. The content of each tab is now loaded not before the tab is opened for the first time. Analogous,
	  if the content of a tab needs to be refreshed, it is only refreshed if the tab is currently showing.
	  If it's not showing, it will be refreshed as soon as it gets shown. This works (at the moment) for
	  all tabs except lineup, players, transfers. Lazy initialization/refresh will noticeable (depending
	  on the number of modules you are using) decrease startup time and reduce the memory footprint of the application.

- Added Tournament matches.
- New icon and startup screen.
- Moved intern help texts and from external files to our wiki page (https://sourceforge.net/apps/trac/ho1/wiki/Manual) and link to it in the menu.
- Made player name column width adjustable on squad tab.
- Added half time results in matches tab.
- Seat distribution is shown in arena section of Statistics tab.
- Stadium prices HT-adapted (7/10/19/35).
- Copying ratings to the clipboard in HT-ML table format now centres all ratings in the table.
- Lowered Overcrowding penalty for lineups with 3 forwards. Attack ratings with 3 forwards should now be closer to the reality.
- TS Forecast Module should now display confidence at the correct level.
- TS Forecast Module: Fixed Loepi curve with a new database, enabled TS and confidence graphs by default and detect if team is still in cup or has qualifiers.
- Maintain Captain and Set piece taker when the line up is reversed.
- The old penalty shooter button is gone. Look for the new tab instead.
- Added ability to reset all positional orders in the line up tab.
- Calculate loyalty and homegrownbonus for set pieces and penalty takers.
- Fixed IFA module not to download matches before the user got the team.
- Removed Shirt number 100 for players without a shirt number.
- Included Loyalty and mother club into csv player export
- ArenaStats initial view corrected (filter setting was "competition matches", while what was displayed was "all matches").
- on HRF import, user will not be asked for every HRF which was already imported if he wants to import it again (checkbox "apply to all")
- user/database configuration form got some polish and went from the Options dialog to the File->Database menu
- database cleanup tool went from the Tools menu to File->Database

Language updates:
-----------------
- Added Japanese as new HO language (thx nomad331).
- Updated Danish translations (thx Max_Schreck).
- Updated English translations (thx edswifa).
- Updated French translations (thx davidatorus).
- Updated Galego translations (thx GZ-grobas).
- Updated German translations (thx Werder0405).
- Updated Hangul (Korean) translations (thx SI-CH).
- Updated Lithuanian translations (thx LA-MJ).
- Updated Nederlands translations (thx BrammieG).
- Updated Persian translations (thx bagher).
- Updated Portugues translations (thx a_cardoso).
- Updated Romanian translations (thx DragosE).
- Updated Spanish translations (thx LarsVegas).

ThemeManager:
------------
- New keys:
	. Icons: imagePanel.background, grassPanel.background, remove, transfer.in, transfer.out, MatchTypeTourneyGrp, MatchTypeTourneyPlayOff, exclamation-red, exclamation, control-double-090, control-double-270, order_set, plays_at_beginning, is_reserve, not_in_lineup, move_up, move_down, move_left, move_right, arrow_circle_double, arrow_move, substitution, clearPositionOrders
	. Colors: matchtype.tourneyGroup.bg, matchtype.tourneyFinals.bg, teamanalyzer.teamlist.league, teamanalyzer.teamlist.cup, teamanalyzer.teamlist.nexttournament, teamanalyzer.teamlist.tournament
- Fatcow-Theme: Added and updated some icons and colors.

Plugin (Module) Updates:
---------------
- Active developed Plugins are now integrated as modules, outdated and not more developed Plugins are NOT integrated/supported anymore.
	. Calendar, Commons, Converter, Feedback, PerformanceOverview, SeriesStats, StarCalc, Teamplanner and YouthClub were NOT integrated.
	. NTHRF, SpecialEvents, TeamAnalyzer, TeamOfTheWeek, TrainingExperience, Transfers, TS Forecast were integrated.
	. ArenaSizer was moved into tools + got more options and statistical section.
	. DevHelp -> SQL Dialog integrated for developers.
	. Evilcard was integrated + got a small rearrangement of panels.
	. ExperienceViewer moved as submodule into Playeranalysis.
	. Flags was not integrated. Functions are integrated in IFA module.
	. HRF-Explorer was moved into tools.
	. IFA was integrated + Panels were rearranged and also optimized.
	. MatchesOverview was not integrated. Functions are integrated in matches tab.
	. Playercompare moved as submodule into Playeranalysis.
	. Transferscout moved as submodule into Transfers.
	. XMLExporter was moved into tools.
- New startup possibility for each module in the options:
	. Autostart = visible after HO-startup. (uses memory)
	. Activated = Not visible after HO-Startup but easy to start with the functions-menu without HO-restart. (uses memory)
	. Deactivated = Not visible after HO-Startup. Can only be made visible through changing the startup option and HO-restart. (uses no memory)
- Possibility added to close a module with the "x"-Button on each tab.


Changelist HO! 1.431
====================
- Added Loyalty and Mother Club Bonus.
 	. Rating prediction is updated.
 	. Added in most places we could think of.
 	. Column preferences are cleared in lineup and squad view due to column changes.

- Theme Manager is added - customize the looks! (Thanks fusselhirn!)
	. See example.zip in the theme folder, especially data.txt and ReadMe.txt inside.

- Download modifications
	. Added better error message when file save on download fails.
	. HRF saving is now optional. You can actually cancel!
	. Reworked Download to abort on failure. Cancel only once!
	. CHPP is now checked for status via it's XML
	. Sanity checks added for downloads of matches...
	. Removed double proxy dialog on download of non chpp files.
	. Can now delete 'corrupted' matches
- Added better error message when database is unable to create its file.
- Fixed sort issue on player tables.
- Fixed localization issue messing up ratings for some locales
- Added new parameter to ratings, called postDelta, which is added after postMulti has been applied (defaults to 0).
- Added two more new parameter to CA ratings calculations only, called playerPostMulti and playerPostDelta, for use in CA player contribution calculations (default to 1 and 0 respectively)
- Modified button behaviour on matches screen to be enabled/disabled appropriately
- Fixed player regainer string not showing.
- Fixed wrong set piece taker on corners
- Added missing match events
- Added rest of HOMainFrame non language string to language files
- Added Hatstats and Loddar stats to Matches view
- Minor fixes and tweaks that is probably not mentioned...
- Download of beta and update via HO now works.

Language updates:
-----------------
- Removed unused strings (thx Seb04 for hunting :) )
- Added Hebrew as new HO language (thx Mod-Transy)
- Added Indonesian as new HO language (thx Zolfaghar)
- Update Bulgarian translations (thx stiflar)
- Update Catalan translations (thx carlesmu)
- Update Chinese translations (thx tianbing)
- Update Czech translations (thx fisero)
- Update Danish translations (thx Max_Schreck)
- Update Finnish translations (thx paragon81)
- Update French translations (thx fierz1998)
- Update Galego translations (thx GZ-grobas)
- Update Georgian translations (thx serjhanti, Rukhadze)
- Update German translations (thx Werder0405)
- Update Hangul(Korean) translations (thx SI-CH)
- Update Hebrew translations (thx Mod-Transy)
- Update Hrvatski(Croatian) translations (thx -Iuve-)
- Update Italiano translations (thx silkevicious)
- Update Lithuanian translations (thx Kacerga)
- Update Magyar translations (thx salt00)
- Update Nederlands translations (thx BrammieG)
- Update Norsk translations (thx ultracool)
- Update Persian translations (thx lvl_Rashid_lvl)
- Update Polish translations (thx aMiUK)
- Update Portugues translations (thx Aristodemos)
- Update PortuguesBrasil translations (thx garcilp)
- Update Romanian translations (thx Spify)
- Update Russian translations (thx Deal_of_Ghost)
- Update Slovak translations (thx Refri)
- Update Spanish translations (thx Baler0)
- Update Spanish_sudamericano translations (thx Baler0)
- Update Srpski + Srpski (lat) translations (thx FoxWMB, ljushaff)
- Update Svenska translations (thx KrustyTheClown, Ehlana)
- Update Turkish translations (thx QUARESMAA7)
- Update Ukranian translations (thx oleh_deneb)
- Update Vlaams translations (thx icarus95)

Plugin Updates:
---------------
- Training Experience v 1.255:
	. Added set piece filter, and removed occurrences of no longer existing general and stamina training
	. Changed icon drawing on Analyzer panel to show values > 99

- Team Analyzer v 2.84:
	. Fixed icon issue with Nimbus skin
	. Match download is updated to work even better

- Special Events v 1.24
	. Added missing events

Themes:
---------------
- Fatcow
	. Added first version (thanks Werder0405)


Changelist HO! 1.430
====================
- Publishing a proper release based on 1.429
- Some minor fixes


Changelist HO! 1.429
====================
- Authentication is now done through oAuth
- Added flags for Cuba, O'zbekiston, Cameroon, and Palestine
- Training defaults are updated (thx Art-Frisson for numbers), and the format of modifying them in options have changed
- Fixed a small error in the Lineup gui with temporary players
- Downloaded matches with individual orders should now display better.
  Matches already in the database can be redownloaded from the matches screen.
- Prediction Offset should work again.
- It is no longer possible to set the keeper at set piece taker.
- The set piece taker is no longer automatically first on the penalty shooter list.
- Changed formation experience ordering according to Hattrick

Language updates:
-----------------
- Update Catalan translations (thx CAT-fike)
- Update Finnish translations (thx -Lupi-)
- Update French translations (thx Butboja)
- Update Galego translations (thx OReiDasSachadas)
- Update German translations (thx Werder0405)
- Update Hrvatski(Croatian) translations (thx Bilke5)
- Update Italiano translations (thx silkevicious)
- Update Nederlands translations (thx BrammieG)
- Update Norsk translations
- Update Polish translations (thx aMiUK)
- Update Portugues translations (thx a_cardoso)
- Update Slovak translations (thx zymbo)
- Update Spanish translations (thx Baler0, garcilp)
- Update Spanish_Sudamericano translations (thx Baler0)
- Update Srpski translations (thx dr_Chokky)
- Update Srpski(lat) translations (thx dr_Chokky)
- Update Vlaams translations (thx icarus95)


Changelist HO! 1.428
====================
- The model is switched to the 553 lineup model. This has affected a lot of the code
- Linup screen now has 14 position boxes, but only 11 can be filled
- Swap ability is added to subs
- Moved positions of captain, set piece taker and reserves to match the HT layout
- Moved buttons for categories, and flips with friends compact the layout
- Small adjustment to output of "copy ratings to clipboard"
- "MiniPosFrame" has indicators for incomplete lineup (and 553 layout)
- Matches screen is updated to display matches in a 553 format
- Use of all CHPP APIs related to match lineups are now at latest API version
- TeamAnalyzer is updated to 553 changes, including lineup screen
- Team of the week plugin interprets the new positions, otherwise no change
- Added crowding penalties in rating calculation. Default is again the default prediction set
- Prediction type can again be selected manually in the lineup screen
- Removed some traces of the now gone economists and gk trainers


Changelist HO! 1.427
====================
- automatically select prediction type (thx Smaug)
- allow new position "Extra def. Forward" to simulate lineups with 3 def. forwards (thx Smaug)
- add optional "Nimbus" look and feel, if available
- update some graphics
- fix bug with fraction digit in fanclub size
- fix home/away determination for DBs with illegal matches (announced but never played)


Changelist HO! 1.426
====================
- fix bug, that prevented HO from starting in special screen configurations
- fix bug in TeamAnalyzer which lead to missing opponent specials and wrong blue player names
- add option to "re-simulate" an old match
- allow openjdk as alternative for the Debian build (thx salt00)
- lineup screen: add feature to copy ratings into the system clipboard
- allow to quick-switch also the keeper
- quick hack to handle HTs changed lineup data
- add handling for new formation experience data
- add new engine prediction types from cirlama

Language updates:
-----------------
- fix Spanish specialty names
- update Serbian translations (thx Chokky)
- add Catalan translations for some plugins (thx CAT-fike)
- update English denominations to solve problems in the player parser (thx salt00)
- add Hangul (Korean) as new HO language (thx to dodegun!)
- fix Danish 'non-existent' denomination (fixes also player parser problems)
- add Persian as new HO language (big thx to lvl_Rashid_lvl!)

Plugin Updates:
---------------
- flagsplugin 3.23 (catch possible exception when getting the country id of an opponent)
- TSForecast 1.01 (fix problems at plugin start with only few datasets)
- add PlayerCompare and HRFExplorer to plugins (thx KickMuck)

Changelist HO! 1.425
====================
- fixed problem of missing match highlights / reports when downloading games with the team analyzer
- fixed problem starting match simulation when own tactic strength is higher than divine
- fixed problems in the transferscout player parser when using IE and the bar style
- added new tactic: Long shots
- updated rating prediction
- updated tactic level prediction
- fixed several problems with HT suddenly adding players without RoleID to XML data
- handle long shot events in match highlights
- show correct tactic strength after loading an stored lineup
- Added a new config option for the default state of the "download matches/schedule" checkboxes on the download dialog
- added a database cleanup tool that removes old matches / HRFs
- Fix bug in stadium statistics (>100% usage)
- Using team experience formula by kopsterkespits as default now
- team captain's value in lineup now shows the predicted team experience using this player as captain
- improve support for non latin characters
- MacOS: HO now uses a modern JavaApplicationStub (no need for Rosetta anymore) and the Aqua toolbars (thx to DerKanzler)

Language updates:
-----------------
- add Slovenian as new HO language (big thx to odemodet!)
- huge update of the Bulgarian translation (thx stiflar)
- improve French translation (thx Bruno)
- fixed Czech skill names
- add "Greeklish" as new HO language (big thx to Angelos!)
- improve Slowak translation (thx refri)
- add Chinese as new HO languag (thx Ryan Li!)

Plugin Updates:
---------------
- SpecialEvents 1.221 (added weather SEs, counters, set pieces...)
- Feedback 0.44 (included in HO 1.425)
- TeamAnalyzer 2.70 (included in HO 1.425, using new formula for rating ratios)

Special Notes:
--------------
We changed the minimum Java version starting with this release to Sun Java 1.5 (aka JRE5).
Please update your Java installation if needed.


Changelist HO! 1.424
====================
- fix problem with "-1" trainings and missing stats
- changed the 5,10 and 50 star symbol a bit (more contrast)
- updated EPV
- reactivated HO auto update
- added training block function
- enhance transfer scout player parser
- added CSV player export
- changed the download process of the match highlights (big thx to GM-Mjoelnir!)
	(we are very hopeful that the problem with the missing match reports is fixed now)
- changed HO.sh to allow spaces in path name ($HODIR and $HOHOME)

- Language updates:
	- small fix in the French translation (thx Bruno)
	- updates for several languages (thx -_duke_-, Chokky, LderMax, mikaelmd, Off_Line, Temur)

Plugin Updates:
---------------
- TeamAnalyzer 2.69 (included in HO 1.424, added new "diff%" column to rating comparison)
- Feedback 0.421 (included in HO 1.424)


Changelist HO! 1.423
====================
- fix problem with training subskill calculation on the first update 2009
- adapt transfer scouts player parser for the new design
	(tested only with the default (long) style at the moment)

- Translation updates:
	- added Georgian as new HO language (thx Temur for his very quick translation)
	- updated Serbian translation (thx chokky - again!)
	- updated Swedish (thx mikaelmd)


Changelist HO! 1.422
====================
- added Windows installer version (thx Flattermann and all translators!)
- Match highlights: added new symbol for indirect free kicks
- added weather SEs to match highlights
- update French language file (thx clemchen)
- stars are now aggregated in groups of 5 stars instead of using single stars
- updated rating prediction
- updated epv
- fix player coloring in match reports after HT redesign
- prevent NPE in match details parser (caused by injury)


Changelist HO! 1.421
====================
- fix a problem with HO! not starting after loading data when there was no league schedule
- update rating prediction parameters
- update training speed calculation
- use new EPV calculation (early beta!)
- change default to show 2 fraction digits
- change default log level to "debug"
- change bash dependent code in HO.sh
- fix problem in TransferScout parser mixing injuries and yellow cards
- add Regainer to TransferScout


Changelist HO! 1.420
====================
- adaptions to CHPP interface changes
- language fix German team confidence translation
- fixed a small bug in the implementation to kopsterkespits' team XP formula
- fixed MacOS preferences problem (prefs not saved when using Command-Q)
- fixed regainer problem (regainer in lineup leads to ArrayOutOfBounds-Exception in RatingPredictionManager)
- Translation updates:
	Srpski and Srpski(lat) v3.6 (thx chokky)
	Portuguese v3.6 (thx Ldermax)
	Turkish v3.6 (thx Yusuf)
	new: Hrvatski (thx Dragan)

Plugin Updates:
---------------
- TeamAnalyzer 2.68 (included in HO 1.420, adaptions to CHPP interface changes)
- Transfers 0.972 (included in HO 1.420, adaptions to CHPP interface changes)
- Flags plugin 3.21 (adaptions to CHPP interface changes, added coolness summary)
- Team of the Week 0.2 (adaptions to CHPP interface changes)
- EvilCard 0.952 (fix sorting by percentages, added Portuguese translation thx LderMax)
- Int.Friendly Analyzer 0.95 (adapt to CHPP changes)


Changelist HO! 1.410
====================
- Updated rating prediction (thx Flattermann and many supporters)
- New training speed calculation including minute based training (thx Flattermann)
- Added days of players age
- Added the "away derby" option to the match lineup and it's prediction
- Show birthdays in training prognosis (Training Experience plugin)
- Added new star formulas (thx HO forum users, esp. chokky and Xell)
- Use simple star rating to determine players best position
- Fix mixed flags of Oman and Al Yaman
- Updated Dutch language to version 3.5 (thx Jeronim0)
- Updated Serbian languages to version 3.5 (thx chokky)
- Updated French language to version 3.5 - fixes also bugs at player skill parsing in the Transfer plugin (thx guitch)
- Automatically enable options league- and team-schedule in download dialog
- Added team XP formula from kopsterkespits (tooltip over average team XP)
- Arena Sizer: use lower fan factors

Plugin Updates:
---------------
- Feedback plugin 0.42 (included in HO 1.410)
- Training Experience plugin 1.25 (included in HO 1.410)
- TeamAnalyzer 2.67 (included in HO 1.410)
- Flags plugin 3.19 (fix flags of Uman + Al Yaman, added Uganda + Maldives, use internal coolness calculation)
- EvilCard plugin 0.951 (fix exception)
- SpecialEvents plugin 1.21 (fix exception)

Special Notes:
--------------
Please start a full subskill recalculation (File / Calculate Subskills) after the installation to benefit of the new training speed calculation.


Changelist HO! 1.400
====================
- Added first implementation of new rating formulas (new match engine)
  BIG THX to Flattermann for his effort - he provided GREAT help!
- Added option to switch between old and new match engine in the lineup panel
- Added correct handling of new fan mood levels
- Added stamina training part (thx Flattermann)
- Changed seat distribution in ArenaSizer (60/23.5/14/2.5)
- Added Quatar and Tanzania and their flags
- Added Srpski (Serbian) [latin and kyrillic] as new HO languages (thx chokky + jablan)
- Update Dutch translation (thx jeronim0)
- Prevent possible NPE with red cards (MatchPlayerRetriever)
- Fix minor bug in language update dialog (duplicate entries)
- Catch possible NPE when getting EPV value of certain players
- Minor bugfixes when loading plugins
- Removed EPV splash screen
- Removed HoFriendly as demanded by CHPPs
- Removed player tab from TeamAnalyzer as demanded by CHPPs
- Small updates for all languages (thx to many HO! forum users)

Plugin Updates:
---------------
- EvilCard 0.95
- Flags plugin 3.16
- InternationalFriendlyAnalyzer (IFA) 0.93
- TeamAnalyzer 2.66 (included in HO 1.400)
- Training Experience 1.24 (included in HO 1.400)
- Transfers plugin 0.971 (included in HO 1.400)

Special Notes:
--------------
HO 1.400 includes a first version to predict the ratings of the new match engine. The quality proved to be quite good at internal tests, but the differences may be a bit higher than for the old engine. Use the prediction offset calculation to get better results and provide feedback in the HO! forum. That way we can enhance the accuracy. Thx!

The 2 most active HO1 developers from the lasst months are on vacation for the next 2 respectively 3 weeks. Thats not planned, but a funny coincidence. It might get a bit silent from HO! side during this time. We expect to have helpful feedback when we're back - especially regardings the new rating predictions.


Changelist HO! 1.399
====================
- Adaption to CHPP changes (worlddetails)
- Added Turkish and Galician as new HO languages (thx Yusuf and Pablo)
- Lineup panel: fixed bug that lead to a "4-4-2 all normal lineup" instead
  of the real last / actual lineup of the team
- Fixed coloring of players in match reports (thx to drake79)
  Note: it's necessary to re-download old matches with buggy coloring
- Player overview: use no fraction digits when visualising differences
  in TSI, salary and market value. Show thousand delimiter instead.
- Lineup panel: added option to simulate the trainer type (thx drake79)
- fixed a bug that prevented further downloads when a match with more
  than 3 injured players occured
- ArenaSizer: 18 Euro income from seats under roof
- Lineup panel: fixed label of Reserve Defender
- HT bug hotfix: copy last supporter mood level to current, if its missing in the XML

Converter Plugin:
-----------------
- Adaption to CHPP change (worlddetails)


Changelist HO! 1.398
====================
- TeamAnalyzer: fix a problem with matches with less than 11 players
- ArenaSizer: use correct prices (like in HO! 1.396)
- include all current flags
- compiled using Java 1.4 class compatibility (hopefully fixes some TA issues
  for Java 1.4 and Mac users)
- change internal URL for Plugin downloads to plugins.hattrickorganizer.net
- removed upload of EPV data
- changed version check to allow a newer version in the local installation
- prevent NPE in MatchPopulator (TeamAnalyzer)
- prevent NSEEx in RatingUtil (Commons)
- prevent NPE in HTCalendarFactory (Commons)

 Special Note 1:
 ---------------
 To those who did not notice yet: with the help of our fellow user Odicin we
 were able to fix the 3rd party plugins "International Friendly Analyzer" and
 "Flags plugin". Both plugins stopped working after the HT/CHPP change last
 November. If you use these plugins and have problems with the update of new
 flags, make sure to have the latest version installed.
 Check this menu: File -> Update -> Plugins -> Normal.

 Special Note 2:
 ---------------
 Thanks to the work of two HO! users Yusuf and Pablo we can offer "Turkish"
 and "Galician(Galego)" as new HO! language soon. Unfortunately both didn't
 make it into HO! 1.398, we're very sorry! But in the next days both should
 be available using the HO! update for language files.
