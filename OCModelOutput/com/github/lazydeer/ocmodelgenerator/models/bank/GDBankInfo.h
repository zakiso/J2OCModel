//
//  GDBankInfo.h
//  TestProject
//
//  Created by Model Generate on 16/12/10
//  Copyright © 2016年 Shanghai HEADING Information Engineering Co., Ltd. All rights reserved.
//


#import <Foundation/Foundation.h>

#import "GDBank.h"



@interface GDBankInfo : GDBank

@property (nonatomic, copy) NSString * cvv2;

@property (nonatomic, assign)BOOL expired;

@property (nonatomic, copy) NSString * cardHolderName;

@property (nonatomic, assign)NSInteger cardId;

@property (nonatomic, copy) NSString * cardHolderId;

@property (nonatomic, copy) NSString * cardHolderMobile;

@property (nonatomic, copy) NSString * bank_name_and_number;


@end
