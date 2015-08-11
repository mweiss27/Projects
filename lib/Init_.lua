if WA_radar then WA_radar_hide()WA_radar.shown=false WA_radar=nil
end WA_trigger=function()if not WA_radar_locked then WA_radar_show()end end wipeDots=wipeDots or function()if WA_dots then for i=1,30do if WA_dots[i]then WA_dots[i]:Hide()WA_dots[i].shown=false end end end end wipeWrought=wipeWrought or function()if WA_wrought_frames then for i=1,15do WA_wrought_frames[i].name=nil WA_wrought_frames[i]:Hide()WA_wrought_frames[i].shown=false end end end wipeShackles=wipeShackles or function()if WA_shackle_frames then for i=1,3do WA_shackle_frames[i].name=nil WA_shackle_frames[i]:Hide()WA_shackle_frames[i].shown=false WA_shackle_texts[i]:Hide()WA_shackle_texts[i].shown=false end end end wipeDots()wipeWrought()wipeShackles()print("Initializing Archimonde Assist")WA_radar_range=40WA_netherBanishId=186952local RAID_CLASS_COLORS=RAID_CLASS_COLORS local BLIP_TEX_COORDS={["WARRIOR"]={0,0.125,0,0.25},["PALADIN"]={0.125,0.25,0,0.25},["HUNTER"]={0.25,0.375,0,0.25},["ROGUE"]={0.375,0.5,0,0.25},["PRIEST"]={0.5,0.625,0,0.25},["DEATHKNIGHT"]={0.625,0.75,0,0.25},["SHAMAN"]={0.75,0.875,0,0.25},["MAGE"]={0.875,1,0,0.25},["WARLOCK"]={0,0.125,0.25,0.5},["DRUID"]={0.25,0.375,0.25,0.5},["MONK"]={0.125,0.25,0.25,0.5}}local UnitPosition=UnitPosition local GetPlayerFacing=GetPlayerFacing local GetUnitName,UnitClass,UnitIsUnit,UnitExists=GetUnitName,UnitClass,UnitIsUnit,UnitExists local sqrt,max,min,sin,cos,atan2,pi=math.sqrt,math.max,math.min,math.sin,math.cos,math.atan2,math.pi local defaultFont=GameFontNormal:GetFont()local sonarSound="Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Sounds\\sonar.ogg"WA_dots={}WA_shackle_frames={}WA_shackle_texts={}WA_wrought_frames={}WA_wrought_test=false WA_wrought_test_frames={}WA_wrought_warning=GetTime()WA_radar=CreateFrame("Frame","WA_Frame",UIParent)WA_radar:SetFrameStrata("DIALOG")WA_radar:SetSize(700,350)if WeakAurasSaved and WeakAurasSaved["displays"]and WeakAurasSaved["displays"]["Archimonde Radar"]then local t=WeakAurasSaved["displays"]["Archimonde Radar"]WA_radar:SetPoint("CENTER",UIParent,"CENTER",t["xOffset"],t["yOffset"])end WA_radar:SetToplevel(true)WA_circle=WA_radar:CreateTexture(nil,"BACKGROUND")WA_circle:SetSize(350,350)WA_circle:SetPoint("CENTER",WA_radar,"CENTER")WA_circle:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")WA_circle:SetVertexColor(.255, .255, .255, .6)WA_circle:SetBlendMode("ADD")WA_radar.circle=WA_circle WA_player=WA_radar:CreateTexture(nil,"OVERLAY")WA_player:SetSize(16,16)WA_player:SetPoint("CENTER",WA_radar,"CENTER")WA_player:SetTexture("Interface\\Addons\\WeakAuras\\PowerAurasMedia\\Auras\\Aura118.tga")RegisterAddonMessagePrefix("AAssist")WA_player:SetVertexColor(1,1,1,1)WA_player:SetBlendMode("ADD")WA_radar_locked=false if WeakAurasSaved["!!aaLocked!!"]~=nil then WA_radar_locked=true end WA_radar.player=WA_player for i=1,30do local dot=WA_radar:CreateTexture(nil,"ARTWORK")dot:SetSize(18,18)dot:SetTexture("Interface\\Minimap\\PartyRaidBlips")dot:Hide()dot.shown=false WA_dots[i]=dot end for i=1,15do local wrought=WA_radar:CreateTexture(nil,"BORDER")wrought:SetTexture("Interface\\line4px")wrought:SetTexCoord(0.5,1,0,1)
wrought:SetVertexColor(0.6,1,0.6,1)wrought:SetBlendMode("ADD")wrought:SetSize(350,350)wrought:Hide()wrought.shown=false WA_wrought_frames[i]=wrought end local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)for i=1,3do local shackleFrame=WA_radar:CreateTexture(nil,"OVERLAY")shackleFrame:SetSize(25*ppy*2,25*ppy*2)
shackleFrame:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")shackleFrame:SetVertexColor(1,0,0, .5)
shackleFrame:Hide()shackleFrame.shown=false WA_shackle_frames[i]=shackleFrame local shackleText=WA_radar:CreateFontString(nil,"OVERLAY")shackleText:SetSize(45,24)shackleText:SetFont(defaultFont,14,"OUTLINE")shackleText:SetText("")shackleText:Hide()shackleText.shown=false WA_shackle_texts[i]=shackleText end WA_radar_setRange=function(range)WA_radar_range=range local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)for i=1,3do local shackleFrame=WA_shackle_frames[i]shackleFrame:SetSize(25*ppy*2,25*ppy*2)
end end WA_radar_hide=function()WA_radar:Hide()WA_circle:Hide()WA_player:Hide()WA_player.shown=false WA_circle.shown=false WA_radar.shown=false end WA_radar_show=function()if WA_radar_locked then WA_radar_hide()return end WA_radar:Show()WA_circle:Show()WA_player:Show()WA_player.shown=true WA_circle.shown=true WA_radar.shown=true end WA_radar_hide()local function pointFromLine(x1,y1,x2,y2,x3,y3)
local px=x2-x1 local py=y2-y1 local pSq=px*px+py*py local u=((x3-x1)*px+(y3-y1)*py)/pSq if u>1then u=1 elseif u<0then u=0 end local x=x1+u*px local y=y1+u*py local dx=x-x3 local dy=y-y3 local dist=sqrt(dx*dx+dy*dy)return dist end local function playerIsBanished()local id=186952
local name="Nether Banish"local type="HARMFUL"local testing=true if testing then name="Prayer of Mending"id=41635type="PLAYER|HELPFUL"end local spellId=select(11,UnitAura("player",name,nil,type))return spellId and spellId==id end WA_radar_pixelsBetween=function(x1,y1,x2,y2)local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)local dx=x2-x1 local dy=y2-y1 local dist=sqrt(dx*dx+dy*dy)return ppy*dist end WA_radar_dist=function(idx)if idx then local name,_,_,_,_,_,_,online,isDead,_,_=GetRaidRosterInfo(idx)if name then if online and not isDead then local dist,valid=UnitDistanceSquared(name)if valid then return math.sqrt(dist)end end end return-1 end end function WA_ping(bTagFrom)for i=1,BNGetNumFriends()do local presenceID,_,battleTag=BNGetFriendInfo(i)if battleTag then if battleTag=="Shenzai#1262"then BNSendGameData(presenceID,"AAssist",GetUnitName("player"))return elseif battleTag and bTagFrom and battleTag~=bTagFrom then BNSendGameData(presenceID,"AAssist","crawl")end end end end WA_ping()WA_removeShackle=function(name)for i=1,3do local shackleFrame=WA_shackle_frames[i]if shackleFrame.name and shackleFrame.name==name then shackleFrame.name=nil shackleFrame:Hide()shackleFrame.shown=false WA_shackle_texts[i]:Hide()WA_shackle_texts[i].shown=false end end end WA_removeWrought=function(name)for i=1,15do local wroughtFrame=WA_wrought_frames[i]if wroughtFrame.focused and wroughtFrame.focused==name then wroughtFrame.focused=nil wroughtFrame.wrought=nil wroughtFrame:Hide()wroughtFrame.shown=false end end end WA_radar_updateWrought=function()if WA_radar_locked then return end if playerIsBanished()then WA_radar_hide()end if not WA_radar.shown then return end local t=WeakAurasSaved["displays"]["Archimonde Radar"]local rotation=(2*pi)-GetPlayerFacing()local sinTheta=sin(rotation)local cosTheta=cos(rotation)local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)WA_radar.circle:SetSize(WA_radar_range*ppy*2,WA_radar_range*ppy*2)local pX,pY,_,pMap=UnitPosition("player")for i=1,15do local wroughtFrame=WA_wrought_frames[i]local wrought=wroughtFrame.wrought local focused=wroughtFrame.focused local focusedMap=wroughtFrame.map if wrought and focused and focusedMap then if UnitExists(wrought)and UnitExists(focused)and not UnitIsUnit(wrought,focused)and pMap==focusedMap then if wrought and focused then local wX,wY,_,wMap=UnitPosition(wrought)local fX,fY,_,fMap=UnitPosition(focused)if wX and wY and fX and fY then WA_drawWrought(i,pX,pY,wX,wY,fX,fY,cosTheta,sinTheta,ppy,k)end end end end end end WA_drawWrought=function(idx,pX,pY,wX,wY,fX,fY,cosTheta,sinTheta,ppy,wroughtName)if not WA_radar.shown then return end local wroughtFrame=WA_wrought_frames[idx]local wroughtName=wroughtFrame.wrought local focusedName=wroughtFrame.focused local dist=min(150,((((fX-wX)^2+(fY-wY)^2)^0.5)+1)*100)wroughtFrame.x=-(wY-pY)wroughtFrame.y=-(wX-pX)local dx=((wroughtFrame.x*cosTheta)-(-wroughtFrame.y*sinTheta))*ppy local dy=((wroughtFrame.x*sinTheta)+(-wroughtFrame.y*cosTheta))*ppy local deltaX=fX-wX local deltaY=fY-wY local angle=atan2(deltaX,deltaY)local polarAngle=-(angle+GetPlayerFacing()+pi)local offsetX=-(175*cos(polarAngle))local offsetY=-(175*sin(polarAngle))local offsetAngle=atan2(offsetX,offsetY)local anchor="BOTTOMLEFT"wroughtFrame:SetPoint("CENTER",WA_radar,"CENTER",dx,dy)wroughtFrame:SetRotation(polarAngle)local fXX=fX+(deltaX*200)local fYY=fY+(deltaY*200)local distFromLine=WA_checkWroughtOverlap(wX,wY,fXX,fYY,pX,pY)if focusedName and wroughtName then local playerName=GetUnitName("player")if focusedName==playerName or wroughtName==playerName then wroughtFrame:SetVertexColor(0.39,1,1,1)
elseif distFromLine>2then wroughtFrame:SetVertexColor(0.6,1,0.6,1)
else if GetTime()-WA_wrought_warning>1then PlaySoundFile(sonarSound,"master")WA_wrought_warning=GetTime()end wroughtFrame:SetVertexColor(1,0,0,1)
end end if not wroughtFrame.shown then wroughtFrame:Show()wroughtFrame.shown=true end end WA_checkWroughtOverlap=function(...)local dist=pointFromLine(...)return dist end if IsInGuild()then SendAddonMessage("AAssist","broadcast","GUILD")end WA_radar_updateShackles=function()if playerIsBanished()then WA_radar_hide()end if WA_radar_locked then return end if not WA_radar.shown then return end local rotation=(2*pi)-GetPlayerFacing()local sinTheta=sin(rotation)local cosTheta=cos(rotation)local pX,pY,_,pMap=UnitPosition("player")local s=""local minNumCheck local idx=1for i=1,3do local shackleFrame=WA_shackle_frames[i]local shackleText=WA_shackle_texts[i]local name=shackleFrame.name if name then s=s..i.."="..name..", "local uX=shackleFrame.locX local uY=shackleFrame.locY local uMap=shackleFrame.map if uX and uY and uMap and pMap==uMap then local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)shackleFrame.x=-(uY-pY)shackleFrame.y=-(uX-pX)local dx=((shackleFrame.x*cosTheta)-(-shackleFrame.y*sinTheta))*ppy local dy=((shackleFrame.x*sinTheta)+(-shackleFrame.y*cosTheta))*ppy shackleFrame:ClearAllPoints()shackleFrame:SetPoint("CENTER",WA_radar,"CENTER",dx,dy)shackleFrame:SetTexture("Interface\\AddOns\\WeakAuras\\Media\\Textures\\Circle_White.tga")local distFromShackle=((pX-uX)^2+(pY-uY)^2)^0.5if distFromShackle>25then shackleFrame:SetVertexColor(0,1,0,0.5)
elseif name==GetUnitName("player")then shackleFrame:SetVertexColor(0.2,0.6,1,0.5)
else shackleFrame:SetVertexColor(1,0,0,0.5)
end shackleFrame:SetDrawLayer("OVERLAY",0)local shX,shY,_,shMap=UnitPosition(name)local pDistFromShackle=max(0,25-((shX-uX)^2+(shY-uY)^2)^0.5)local myDistFromShackle=((pX-uX)^2+(pY-uY)^2)^0.5local numCheck=(WA_radar_range)-myDistFromShackle-25if numCheck<0then WA_radar_setRange(WA_radar_range-numCheck)end if not minNumCheck then minNumCheck=numCheck elseif numCheck<minNumCheck then minNumCheck=numCheck end shackleText:SetFont(defaultFont,14,"OUTLINE")shackleText:SetText(string.format("%d",pDistFromShackle))shackleText:SetPoint("CENTER",WA_radar,"CENTER",dx,dy)if not shackleFrame.shown then shackleFrame:Show()shackleFrame.shown=true shackleText:Show()shackleText.shown=true end end end end if minNumCheck and minNumCheck>0then WA_radar_setRange(math.max(40,WA_radar_range-minNumCheck))end end WA_radar_updateDots=function()if playerIsBanished()then WA_radar_hide()end if not WA_radar.shown then return end if WA_radar_locked then return end local t=WeakAurasSaved["displays"]["Archimonde Radar"]local rotation=(2*pi)-GetPlayerFacing()local sinTheta=sin(rotation)local cosTheta=cos(rotation)local ppy=min(WA_radar:GetWidth(),WA_radar:GetHeight())/(WA_radar_range*3)WA_radar.circle:SetSize(WA_radar_range*ppy*2,WA_radar_range*ppy*2)local pX,pY,_,pMap=UnitPosition("player")for i=1,30do local dot=WA_dots[i]if dot then local dist=WA_radar_dist(i)local unit if IsInRaid()then unit="raid"..i elseif IsInGroup()then unit="party"..i else if dot.shown then dot:Hide()dot.shown=false end return end local uX,uY,_,uMap=UnitPosition(unit)if UnitExists(unit)and pMap==uMap then local _,class=UnitClass(unit)if not UnitIsUnit(unit,"player")then if dist~=-1and dist<=WA_radar_range then WA_drawDot(dot,pX,pY,uX,uY,cosTheta,sinTheta,ppy,class)else if dot.shown then dot:Hide()dot.shown=false end end end else if dot.shown then dot:Hide()dot.shown=false end end end end end WA_drawDot=function(dot,pX,pY,uX,uY,cosTheta,sinTheta,ppy,class)if not WA_radar.shown then return end dot.x=-(uY-pY)dot.y=-(uX-pX)dot.range=dist local dx=((dot.x*cosTheta)-(-dot.y*sinTheta))*ppy local dy=((dot.x*sinTheta)+(-dot.y*cosTheta))*ppy dot:ClearAllPoints()dot:SetPoint("CENTER",WA_radar,"CENTER",dx,dy)dot:SetTexture("Interface\\Minimap\\PartyRaidBlips")dot:SetTexCoord(BLIP_TEX_COORDS[class][1],BLIP_TEX_COORDS[class][2],BLIP_TEX_COORDS[class][3],BLIP_TEX_COORDS[class][4])dot:SetSize(18,18)dot:SetDrawLayer("OVERLAY",0)if not dot.shown then dot:Show()dot.shown=true end end
