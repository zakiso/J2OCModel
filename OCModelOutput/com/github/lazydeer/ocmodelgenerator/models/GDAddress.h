//
//  GDAddress.h
//  TestProject
//
//  Created by Model Generate on 16/12/10
//  Copyright © 2016年 Shanghai HEADING Information Engineering Co., Ltd. All rights reserved.
//


#import <Foundation/Foundation.h>



@interface GDAddress : NSObject

@property (nonatomic, copy) NSString * country;

@property (nonatomic, copy) NSString * address;

@property (nonatomic, copy) NSString * province;

@property (nonatomic, copy) NSString * city;

@property (nonatomic, copy) NSString * street;

@property (nonatomic, copy) NSString * district;

@property (nonatomic, assign)NSInteger postcode;

@property (nonatomic, copy) NSString * detailAddress;


@end
