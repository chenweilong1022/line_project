package io.renren.modules.ltt.service;

import io.renren.modules.ltt.dto.*;
import io.renren.modules.ltt.vo.*;

/**
 * @author liuyuchan
 * @email liuyuchan286@gmail.com
 * @date 2023/11/30 21:56
 */
public interface LineService {

    LineRegisterVO lineRegister(LineRegisterDTO lineRegisterDTO);
//    /api/v1/account/issueLiffView
    LineRegisterVO issueLiffView(IssueLiffViewDTO issueLiffViewDTO);
//    api/v1/account/refreshAccessToken
    RefreshAccessTokenVO refreshAccessToken(RefreshAccessTokenDTO issueLiffViewDTO);
    RegisterResultVO registerResult(RegisterResultDTO registerResultDTO);
    SMSCodeVO smsCode(SMSCodeDTO smsCodeDTO);
    SyncLineTokenVO SyncLineTokenDTO(SyncLineTokenDTO syncLineTokenDTO);

    SearchPhoneVO searchPhone(SearchPhoneDTO searchPhoneDTO);
    LineRegisterVO createGroupMax(CreateGroupMax createGroupMax);
    SearchPhoneVO findAndAddContactsByPhone(SearchPhoneDTO searchPhoneDTO);
    SearchPhoneVO addFriendsByMid(AddFriendsByMid addFriendsByMid);

    CreateGroupResultVO createGroupResult(RegisterResultDTO registerResultDTO);
//    /api/v1/account/openApp
    LineRegisterVO openApp(OpenApp openApp);
//    api/v1/account/openAppResult
    OpenAppResult openAppResult(RegisterResultDTO registerResultDTO);
    //http://137.184.112.207:22117/api/v1/work/syncContents
    LineRegisterVO syncContents(SyncContentsDTO syncContentsDTO);
    //http://137.184.112.207:22117/api/v1/work/syncContentsResult
    SyncContentsResultVO syncContentsResult(SyncContentsResultDTO syncContentsResultDTO);
//    http://137.184.112.207:22117/api/v1/work/getChats
    GetChatsVO getChats(GetChatsDTO getChatsDTO);
}
