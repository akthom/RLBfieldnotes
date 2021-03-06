# RLBfieldnotes
scripts to parse and clean field notebooks from Ranch La Brea

24 July 2016
-------
Notes below migrated from former project files

input: 378 pages of the 2004 field season

Separating entries by day and author is easy enough, because each day starts with the same "Day Month Year comma" string, and each author is identified by "FirstInitial. MiddleInitial. LastName colon".  

Measurements will need to be pulled out differently 
 
We have 2 different types of field measurements.  Our standard and most common are "two point" measurements:
 
`RLP 1938A     91/H-8`

`BD = 14'2 ½" to 14'2 ¾"`

`N  = 18 ½" to 19"`

`W  = 25" to 26" P to D`

`Aves humerus (prox end)`
 
and the slightly more detailed "three point" measurements:
 
`RLP 1758A     91/J-8`
`     Px        Dt        Tub`
`BD = 13'3 ½"  13'3 ¾"   13'4"`
`N  = 4 ¾"     8 ¾"      5"`
`W  = 34"      32"       33 ¼"`
`Smilodon rib`
 
The abbreviations on the second line indicate anatomical points.  

- there are frequently sections of prose text (field notes and additional descriptions) inserted between measurements. 
- Phrases that might be used as identifiers (such as "RLP ####") are sometimes used out of context (e.g "RLP ####" might be used in a "diary" prose text entry to describe the grid being worked in, in addition to being the first part of every measurement)
-The first quarter or so of these notes makes heavy use of the "strike thru" font style to indicate that something was crossed out and changed.

Might want to eventually add a marker of some sort to flag each measurement.  This would definitely be possible -- especially if it would be less time consuming add the marker than it would to write a complex script.
